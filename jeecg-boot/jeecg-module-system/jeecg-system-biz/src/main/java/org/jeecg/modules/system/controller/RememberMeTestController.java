package org.jeecg.modules.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RememberMeUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RememberMe测试控制器
 * 用于测试和验证RememberMe功能的修复效果
 * 
 * @author lingma
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/test/rememberme")
public class RememberMeTestController {

    /**
     * 测试RememberMe状态
     * 
     * @param request HTTP请求
     * @return RememberMe状态信息
     */
    @GetMapping("/status")
    public Result<String> getRememberMeStatus(HttpServletRequest request) {
        try {
            String status = RememberMeUtil.getRememberMeStatus();
            return Result.OK("RememberMe状态获取成功", status);
        } catch (Exception e) {
            log.error("获取RememberMe状态失败", e);
            return Result.error("获取RememberMe状态失败: " + e.getMessage());
        }
    }

    /**
     * 验证RememberMe Cookie
     * 
     * @param request HTTP请求
     * @return Cookie验证结果
     */
    @GetMapping("/validate")
    public Result<String> validateRememberMeCookie(HttpServletRequest request) {
        try {
            String validationResult = RememberMeUtil.validateRememberMeCookie(request);
            return Result.OK("Cookie验证完成", validationResult);
        } catch (Exception e) {
            log.error("验证RememberMe Cookie失败", e);
            return Result.error("验证失败: " + e.getMessage());
        }
    }

    /**
     * 清除RememberMe状态
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 操作结果
     */
    @PostMapping("/clear")
    public Result<String> clearRememberMeState(HttpServletRequest request, HttpServletResponse response) {
        try {
            RememberMeUtil.forceClearRememberMe(request, response);
            return Result.OK("RememberMe状态已清除");
        } catch (Exception e) {
            log.error("清除RememberMe状态失败", e);
            return Result.error("清除失败: " + e.getMessage());
        }
    }

    /**
     * 检查并清理无效Cookie
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 操作结果
     */
    @PostMapping("/check-and-clear")
    public Result<Boolean> checkAndClearInvalidCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean cleaned = RememberMeUtil.checkAndClearInvalidCookie(request, response);
            String message = cleaned ? "发现并清理了无效的RememberMe Cookie" : "RememberMe Cookie状态正常";
            return Result.OK(message, cleaned);
        } catch (Exception e) {
            log.error("检查RememberMe Cookie失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 模拟RememberMe异常场景
     * 仅供测试使用
     * 
     * @return 模拟的异常信息
     */
    @GetMapping("/simulate-error")
    public Result<String> simulateRememberMeError() {
        try {
            // 这里可以添加一些测试逻辑来模拟RememberMe异常
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                boolean isRemembered = subject.isRemembered();
                boolean isAuthenticated = subject.isAuthenticated();
                String message = String.format("当前状态 - Remembered: %s, Authenticated: %s", isRemembered, isAuthenticated);
                return Result.OK("模拟测试完成", message);
            }
            return Result.OK("模拟测试完成", "无用户主体");
        } catch (Exception e) {
            log.error("模拟RememberMe异常时出错", e);
            return Result.error("模拟测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取Shiro Subject信息
     * 
     * @return Subject信息
     */
    @GetMapping("/subject-info")
    public Result<String> getSubjectInfo() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                StringBuilder info = new StringBuilder();
                info.append("Principal: ").append(subject.getPrincipal()).append("\n");
                info.append("Authenticated: ").append(subject.isAuthenticated()).append("\n");
                info.append("Remembered: ").append(subject.isRemembered()).append("\n");
                info.append("Session ID: ").append(subject.getSession(false) != null ? subject.getSession().getId() : "No session");
                return Result.OK("Subject信息获取成功", info.toString());
            }
            return Result.OK("Subject信息获取成功", "No subject available");
        } catch (Exception e) {
            log.error("获取Subject信息失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
}
