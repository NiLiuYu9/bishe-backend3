package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.api.platform.common.constant.AuthConstants;
import com.api.platform.common.service.InnerUserService;
import com.api.platform.common.vo.InvokeUserVO;
import com.api.platform.gateway.ratelimit.RateLimiter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int DEFAULT_CAPACITY = 100;
    private static final int DEFAULT_REFILL_RATE = 10;

    @Autowired
    private RateLimiter rateLimiter;

    @DubboReference
    private InnerUserService innerUserService;

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
            return chain.filter(exchange);
        }

        Integer callLimit = exchange.getAttribute("callLimit");
        int capacity = callLimit != null && callLimit > 0 ? callLimit : DEFAULT_CAPACITY;
        int refillRate = capacity;

        String rateLimitKey = buildRateLimitKey(userId, path);
        
        boolean allowed = rateLimiter.tryAcquire(rateLimitKey, capacity, refillRate);
        
        if (!allowed) {
            String message = callLimit != null && callLimit > 0 
                    ? "已达到API调用频率限制(" + callLimit + "次/分钟)，请稍后再试"
                    : "请求过于频繁，请稍后再试";
            return handleRateLimitExceeded(exchange, message);
        }

        return chain.filter(exchange);
    }

    private Long resolveUserId(ServerHttpRequest request) {
        String userIdHeader = request.getHeaders().getFirst(AuthConstants.USER_ID_HEADER);
        if (StrUtil.isNotBlank(userIdHeader)) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的userId header: {}", userIdHeader);
            }
        }

        String accessKey = request.getHeaders().getFirst(AuthConstants.ACCESS_KEY_HEADER);
        if (StrUtil.isNotBlank(accessKey)) {
            InvokeUserVO user = innerUserService.getInvokeUser(accessKey);
            if (user != null) {
                return user.getId();
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

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("message", message);
        result.put("data", null);

        String json;
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            json = "{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\",\"data\":null}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
