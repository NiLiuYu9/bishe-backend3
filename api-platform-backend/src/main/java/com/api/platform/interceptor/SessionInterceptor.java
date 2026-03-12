package com.api.platform.interceptor;

import com.api.platform.constants.SessionConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session拦截器
 * 
 * 验证用户是否已登录，未登录返回401状态码
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConstants.USER_ID) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或session已过期\",\"data\":null}");
            return false;
        }
        request.setAttribute(SessionConstants.USER_ID, session.getAttribute(SessionConstants.USER_ID));
        request.setAttribute(SessionConstants.USERNAME, session.getAttribute(SessionConstants.USERNAME));
        return true;
    }

}
