package com.api.platform.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AccessControlFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList(
            "127.0.0.1",
            "localhost",
            "0:0:0:0:0:0:0:1",
            "::1"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        String sourceAddress = request.getLocalAddress() != null 
                ? request.getLocalAddress().getHostString() 
                : null;
        
        if (sourceAddress != null && !IP_WHITE_LIST.contains(sourceAddress)) {
            log.warn("IP不在白名单中: {}", sourceAddress);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isWhitePath(String path) {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/actuator/health");
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
