package com.api.platform.interceptor;

import com.api.platform.constants.SessionConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session登录校验拦截器
 * <p>核心职责：验证用户是否已登录，未登录返回401状态码。
 * 对OPTIONS预检请求直接放行（CORS支持），已登录请求将Session属性复制到Request属性中，
 * 便于后续的限流拦截器获取用户ID。</p>
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * 请求预处理：校验登录状态
     * <p>1. OPTIONS请求直接放行（浏览器CORS预检）；
     * 2. 检查Session中是否存在userId；
     * 3. 未登录返回401 JSON响应；
     * 4. 已登录将userId和username复制到Request属性，供后续拦截器使用。</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-放行，false-拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行CORS预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConstants.USER_ID) == null) {
            // 未登录，返回401状态码和JSON错误信息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或session已过期\",\"data\":null}");
            return false;
        }
        // 将Session属性复制到Request属性，供限流拦截器等后续组件使用
        request.setAttribute(SessionConstants.USER_ID, session.getAttribute(SessionConstants.USER_ID));
        request.setAttribute(SessionConstants.USERNAME, session.getAttribute(SessionConstants.USERNAME));
        return true;
    }

}
