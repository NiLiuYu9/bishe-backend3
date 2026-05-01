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

/**
 * 限流拦截器
 * <p>核心职责：基于@RateLimit注解和令牌桶算法，对接口请求进行访问频率限制。
 * 优先检查方法级注解，再检查类级注解；以用户ID+URI为维度进行限流，
 * 超出限制返回429状态码。</p>
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 请求预处理：检查限流注解并执行限流判断
     * <p>1. 非Controller方法直接放行；
     * 2. 查找@RateLimit注解（方法级优先，其次类级）；
     * 3. 无注解直接放行；
     * 4. 解析用户ID，构建限流Key，尝试获取令牌；
     * 5. 令牌不足返回429响应。</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-放行，false-限流拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 优先检查方法级@RateLimit注解
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            // 方法级无注解，检查类级注解
            rateLimit = handlerMethod.getBeanType().getAnnotation(RateLimit.class);
        }

        if (rateLimit == null) {
            return true;
        }

        // 解析当前用户ID（从Session或Request属性中获取）
        Long userId = resolveUserId(request);
        if (userId == null) {
            sendRateLimitResponse(response, "无法识别用户身份，请先登录");
            return false;
        }

        // 构建限流Key：用户ID+URI（或自定义Key）
        String key = buildKey(userId, request.getRequestURI(), rateLimit);

        // 尝试获取令牌
        boolean allowed = rateLimiter.tryAcquire(key, rateLimit.capacity(), rateLimit.refillRate());

        if (!allowed) {
            sendRateLimitResponse(response, rateLimit.message());
            return false;
        }

        return true;
    }

    /**
     * 解析当前用户ID
     * <p>优先从Session获取，其次从Request属性获取（SessionInterceptor已复制）。</p>
     *
     * @param request HTTP请求
     * @return 用户ID，未登录时返回null
     */
    private Long resolveUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
            if (userId != null) {
                return userId;
            }
        }

        // 从Request属性获取（SessionInterceptor已将userId复制到request）
        Long userId = (Long) request.getAttribute(SessionConstants.USER_ID);
        return userId;
    }

    /**
     * 构建限流Key
     * <p>如果注解指定了自定义key则使用自定义key，否则使用请求URI。</p>
     *
     * @param userId    用户ID
     * @param uri       请求URI
     * @param rateLimit 限流注解
     * @return 限流Key字符串
     */
    private String buildKey(Long userId, String uri, RateLimit rateLimit) {
        String customKey = rateLimit.key();
        if (customKey != null && !customKey.isEmpty()) {
            return "user:" + userId + ":" + customKey;
        }
        return "user:" + userId + ":" + uri;
    }

    /**
     * 发送限流响应（HTTP 429）
     *
     * @param response HTTP响应
     * @param message  限流提示信息
     */
    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.failed(429, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
