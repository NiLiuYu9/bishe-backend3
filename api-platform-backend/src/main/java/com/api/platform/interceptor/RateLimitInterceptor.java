package com.api.platform.interceptor;

import com.api.platform.annotation.RateLimit;
import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.ratelimit.RateLimiter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            rateLimit = handlerMethod.getBeanType().getAnnotation(RateLimit.class);
        }

        if (rateLimit == null) {
            return true;
        }

        Long userId = resolveUserId(request);
        if (userId == null) {
            sendRateLimitResponse(response, "无法识别用户身份，请先登录");
            return false;
        }

        String key = buildKey(userId, request.getRequestURI(), rateLimit);

        boolean allowed = rateLimiter.tryAcquire(key, rateLimit.capacity(), rateLimit.refillRate());

        if (!allowed) {
            sendRateLimitResponse(response, rateLimit.message());
            return false;
        }

        return true;
    }

    private Long resolveUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
            if (userId != null) {
                return userId;
            }
        }

        Long userId = (Long) request.getAttribute(SessionConstants.USER_ID);
        return userId;
    }

    private String buildKey(Long userId, String uri, RateLimit rateLimit) {
        String customKey = rateLimit.key();
        if (customKey != null && !customKey.isEmpty()) {
            return "user:" + userId + ":" + customKey;
        }
        return "user:" + userId + ":" + uri;
    }

    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.failed(429, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
