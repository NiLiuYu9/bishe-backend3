package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.api.platform.common.constant.AuthConstants;
import com.api.platform.gateway.ratelimit.RateLimiter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Rate limit filter - 4th ring in gateway filter chain (Order=2)
 *
 * Responsibility: Token bucket rate limiting based on Redis + Lua script to prevent malicious high-frequency API calls
 * Rate limit rule: Fixed capacity=2, refillRate=2 (max 2 requests per second per user per path)
 * Returns 429 Too Many Requests when rate limit exceeded
 * Returns 401 Unauthorized when user identity cannot be resolved
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int RATE_LIMIT_CAPACITY = 2;
    private static final int RATE_LIMIT_REFILL_RATE = 2;

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        Long userId = resolveUserId(request);
        if (userId == null) {
            return handleUnauthorized(exchange);
        }

        String rateLimitKey = buildRateLimitKey(userId, path);

        boolean allowed = rateLimiter.tryAcquire(rateLimitKey, RATE_LIMIT_CAPACITY, RATE_LIMIT_REFILL_RATE);

        if (!allowed) {
            return handleRateLimitExceeded(exchange);
        }

        return chain.filter(exchange);
    }

    private Long resolveUserId(ServerHttpRequest request) {
        String userIdHeader = request.getHeaders().getFirst(AuthConstants.USER_ID_HEADER);
        if (StrUtil.isNotBlank(userIdHeader)) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("Invalid userId header: {}", userIdHeader);
            }
        }
        return null;
    }

    private String buildRateLimitKey(Long userId, String path) {
        return "user:" + userId + ":" + path;
    }

    private boolean isWhitePath(String path) {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/actuator/health") ||
               path.startsWith("/test/");
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", "无法识别用户身份");
        result.put("data", null);

        String json;
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            json = "{\"code\":401,\"message\":\"无法识别用户身份\",\"data\":null}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("message", "API调用频率超限(每秒最多2次)，请稍后再试");
        result.put("data", null);

        String json;
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            json = "{\"code\":429,\"message\":\"API调用频率超限(每秒最多2次)，请稍后再试\",\"data\":null}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
