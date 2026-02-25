package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.CryptoException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

/**
 * RememberMe工具类
 * 处理RememberMe相关的安全问题和异常
 * 
 * @author lingma
 * @since 2024-01-01
 */
@Slf4j
@Component
public class RememberMeUtil {

    /**
     * 清除当前用户的RememberMe Cookie
     * 当检测到RememberMe相关异常时调用此方法
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    public static void clearRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            String contextPath = request.getContextPath();

            // clear cookie on root path
            javax.servlet.http.Cookie rootCookie = new javax.servlet.http.Cookie("rememberMe", "");
            rootCookie.setMaxAge(0);
            rootCookie.setPath("/");
            rootCookie.setHttpOnly(true);
            response.addCookie(rootCookie);

            // clear cookie on application context path
            if (contextPath != null && contextPath.length() > 0 && !"/".equals(contextPath)) {
                javax.servlet.http.Cookie contextCookie = new javax.servlet.http.Cookie("rememberMe", "");
                contextCookie.setMaxAge(0);
                contextCookie.setPath(contextPath);
                contextCookie.setHttpOnly(true);
                response.addCookie(contextCookie);
            }
            log.info("RememberMe cookie cleared successfully");
        } catch (Exception e) {
            log.error("Failed to clear RememberMe cookie", e);
        }
    }

    /**
     * 检查RememberMe Cookie是否存在问题
     * 
     * @param request HTTP请求对象
     * @return true表示Cookie可能存在问题，false表示正常
     */
    public static boolean isRememberMeCookieInvalid(HttpServletRequest request) {
        try {
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if ("rememberMe".equals(cookie.getName())) {
                        String cookieValue = cookie.getValue();
                        if (cookieValue != null && cookieValue.length() > 0) {
                            if ("deleteMe".equalsIgnoreCase(cookieValue)) {
                                return true;
                            }
                            // 检查Cookie值的基本格式
                            if (cookieValue.length() < 16) {
                                log.warn("RememberMe cookie value too short: {}", cookieValue.length());
                                return true;
                            }
                            
                            // 尝试Base64解码检查格式
                            try {
                                byte[] decoded = Base64.getDecoder().decode(cookieValue);
                                if (decoded.length < 16) {
                                    log.warn("RememberMe cookie decoded bytes too short: {}", decoded.length);
                                    return true;
                                }
                            } catch (IllegalArgumentException e) {
                                log.warn("RememberMe cookie value is not valid Base64 encoded");
                                return true;
                            }
                        }
                        break;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking RememberMe cookie validity", e);
            return true;
        }
    }

    /**
     * 处理RememberMe相关的CryptoException异常
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param exception 异常对象
     */
    public static void handleCryptoException(HttpServletRequest request, 
                                           HttpServletResponse response, 
                                           CryptoException exception) {
        log.warn("Handling RememberMe CryptoException: {}", exception.getMessage());
        
        // 记录详细的异常信息
        log.debug("CryptoException stack trace:", exception);
        
        // 清除有问题的RememberMe Cookie
        clearRememberMeCookie(request, response);
        
        // 如果当前用户已登录，执行登出操作
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null && subject.isAuthenticated()) {
                subject.logout();
                log.info("User logged out due to RememberMe crypto exception");
            }
        } catch (Exception e) {
            log.error("Error during logout after crypto exception", e);
        }
    }

    /**
     * 生成安全的RememberMe密钥
     * 
     * @return Base64编码的16字节密钥
     */
    public static String generateSecureKey() {
        try {
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            log.error("Failed to generate secure key", e);
            return null;
        }
    }

    /**
     * 验证RememberMe密钥的安全性
     * 
     * @param base64Key Base64编码的密钥
     * @return true表示密钥安全，false表示不安全
     */
    public static boolean isKeySecure(String base64Key) {
        if (base64Key == null || base64Key.isEmpty()) {
            return false;
        }
        
        try {
            byte[] key = Base64.getDecoder().decode(base64Key);
            // 检查密钥长度是否为16字节
            if (key.length != 16 && key.length != 24 && key.length != 32) {
                log.warn("RememberMe key length is not 16/24/32 bytes: {}", key.length);
                return false;
            }
            
            // 检查是否使用了默认的不安全密钥
            String defaultKey = "kPH+bIxk5D2deZiIxcaaaA==";
            if (base64Key.equals(defaultKey)) {
                log.warn("Using default RememberMe key - consider changing for production");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error validating RememberMe key", e);
            return false;
        }
    }

    /**
     * 检查并清理无效的RememberMe Cookie
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return true表示清理了无效Cookie，false表示Cookie正常
     */
    public static boolean checkAndClearInvalidCookie(HttpServletRequest request, HttpServletResponse response) {
        if (isRememberMeCookieInvalid(request)) {
            clearRememberMeCookie(request, response);
            return true;
        }
        return false;
    }

    /**
     * 强制清除RememberMe状态
     * 包括Cookie和Shiro Session中的RememberMe信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    public static void forceClearRememberMe(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 清除Cookie
            clearRememberMeCookie(request, response);
            
            // 清除Shiro中的RememberMe状态
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                subject.logout();
                log.info("Force cleared RememberMe state for user");
            }
        } catch (Exception e) {
            log.error("Error force clearing RememberMe state", e);
        }
    }

    /**
     * 验证RememberMe Cookie的完整性
     * 
     * @param request HTTP请求对象
     * @return 验证结果信息
     */
    public static String validateRememberMeCookie(HttpServletRequest request) {
        try {
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return "No cookies found";
            }
            
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("rememberMe".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value == null || value.isEmpty()) {
                        return "RememberMe cookie value is empty";
                    }
                    
                    if (value.length() < 16) {
                        return "RememberMe cookie value too short: " + value.length() + " bytes";
                    }
                    
                    try {
                        Base64.getDecoder().decode(value);
                        return "RememberMe cookie is valid";
                    } catch (IllegalArgumentException e) {
                        return "RememberMe cookie value is not valid Base64: " + e.getMessage();
                    }
                }
            }
            return "RememberMe cookie not found";
        } catch (Exception e) {
            log.error("Error validating RememberMe cookie", e);
            return "Error validating cookie: " + e.getMessage();
        }
    }

    /**
     * 获取当前RememberMe状态信息
     * 
     * @return RememberMe状态描述
     */
    public static String getRememberMeStatus() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                boolean remembered = subject.isRemembered();
                boolean authenticated = subject.isAuthenticated();
                
                StringBuilder status = new StringBuilder();
                status.append("Authenticated: ").append(authenticated);
                status.append(", Remembered: ").append(remembered);
                
                if (remembered && !authenticated) {
                    status.append(" (RememberMe only)");
                } else if (authenticated) {
                    status.append(" (Fully authenticated)");
                }
                
                return status.toString();
            }
            return "No subject available";
        } catch (Exception e) {
            log.error("Error getting RememberMe status", e);
            return "Error retrieving status";
        }
    }
}
