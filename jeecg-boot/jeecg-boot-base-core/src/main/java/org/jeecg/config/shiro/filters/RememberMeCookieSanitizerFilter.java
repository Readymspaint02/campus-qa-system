package org.jeecg.config.shiro.filters;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RememberMeUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clean invalid rememberMe cookie before Shiro creates Subject.
 */
@Slf4j
public class RememberMeCookieSanitizerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (RememberMeUtil.isRememberMeCookieInvalid(request)) {
            RememberMeUtil.clearRememberMeCookie(request, response);
            log.warn("Invalid rememberMe cookie detected and cleared before Shiro subject creation");
            filterChain.doFilter(new RememberMeCookieStrippedRequest(request), response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static class RememberMeCookieStrippedRequest extends HttpServletRequestWrapper {
        private final Cookie[] cookiesWithoutRememberMe;

        RememberMeCookieStrippedRequest(HttpServletRequest request) {
            super(request);
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                this.cookiesWithoutRememberMe = cookies;
                return;
            }
            List<Cookie> filtered = new ArrayList<>();
            for (Cookie cookie : cookies) {
                if (!"rememberMe".equals(cookie.getName())) {
                    filtered.add(cookie);
                }
            }
            this.cookiesWithoutRememberMe = filtered.isEmpty() ? null : filtered.toArray(new Cookie[0]);
        }

        @Override
        public Cookie[] getCookies() {
            return cookiesWithoutRememberMe;
        }
    }
}
