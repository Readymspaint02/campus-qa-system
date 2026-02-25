package org.jeecg.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.jeecg.common.util.RememberMeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A safer rememberMe manager that tolerates malformed/stale rememberMe cookies.
 * It clears invalid cookies and never propagates decrypt exceptions to avoid log flooding.
 */
@Slf4j
public class SafeCookieRememberMeManager extends CookieRememberMeManager {

    @Override
    public PrincipalCollection getRememberedPrincipals(SubjectContext subjectContext) {
        sanitizeInvalidRememberMeCookie(subjectContext);
        try {
            byte[] bytes = getRememberedSerializedIdentity(subjectContext);
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return convertBytesToPrincipals(bytes, subjectContext);
        } catch (Exception ex) {
            clearRememberMeState(subjectContext, ex);
            return null;
        }
    }

    private void sanitizeInvalidRememberMeCookie(SubjectContext subjectContext) {
        HttpServletRequest request = getRequest(subjectContext);
        HttpServletResponse response = getResponse(subjectContext);
        if (request == null || response == null) {
            return;
        }
        if (RememberMeUtil.isRememberMeCookieInvalid(request)) {
            RememberMeUtil.clearRememberMeCookie(request, response);
            forgetIdentity(subjectContext);
            log.warn("Invalid rememberMe cookie detected and cleared in SafeCookieRememberMeManager");
        }
    }

    private void clearRememberMeState(SubjectContext subjectContext, Exception ex) {
        try {
            HttpServletRequest request = getRequest(subjectContext);
            HttpServletResponse response = getResponse(subjectContext);
            if (request != null && response != null) {
                RememberMeUtil.clearRememberMeCookie(request, response);
            }
            forgetIdentity(subjectContext);
            log.warn("rememberMe parse failed and was cleared: {}", ex.getMessage());
            log.debug("rememberMe parse exception detail", ex);
        } catch (Exception clearEx) {
            log.warn("rememberMe cleanup failed after parse exception: {}", clearEx.getMessage());
            log.debug("rememberMe cleanup exception detail", clearEx);
        }
    }

    private HttpServletRequest getRequest(SubjectContext subjectContext) {
        if (!(subjectContext instanceof WebSubjectContext)) {
            return null;
        }
        WebSubjectContext webSubjectContext = (WebSubjectContext) subjectContext;
        Object servletRequest = webSubjectContext.getServletRequest();
        if (servletRequest instanceof HttpServletRequest) {
            return (HttpServletRequest) servletRequest;
        }
        return null;
    }

    private HttpServletResponse getResponse(SubjectContext subjectContext) {
        if (!(subjectContext instanceof WebSubjectContext)) {
            return null;
        }
        WebSubjectContext webSubjectContext = (WebSubjectContext) subjectContext;
        Object servletResponse = webSubjectContext.getServletResponse();
        if (servletResponse instanceof HttpServletResponse) {
            return (HttpServletResponse) servletResponse;
        }
        return null;
    }
}

