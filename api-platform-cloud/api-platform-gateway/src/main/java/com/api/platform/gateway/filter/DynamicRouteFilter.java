package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * 动态路由过滤器 —— 网关过滤器链第4环（Order=2）
 *
 * 职责：根据接口信息中的 targetUrl 动态路由请求到目标API服务
 * 将原始请求的路径替换为接口配置的 targetUrl，实现API调用的转发
 * 例如：请求 /invoke/api_1 → 转发到 http://mock-api:8101/text/process
 */
@Slf4j
@Component
public class DynamicRouteFilter implements GlobalFilter, Ordered {

    public static final String TARGET_URL_ATTR = "targetUrl";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String targetUrl = exchange.getAttribute(TARGET_URL_ATTR);

        if (StrUtil.isBlank(targetUrl)) {
            return chain.filter(exchange);
        }

        try {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String query = request.getURI().getQuery();

            StringBuilder uriBuilder = new StringBuilder(targetUrl);
            uriBuilder.append(path);
            if (StrUtil.isNotBlank(query)) {
                uriBuilder.append("?").append(query);
            }

            URI targetUri = URI.create(uriBuilder.toString());
            log.info("动态路由: {} -> {}", request.getURI(), targetUri);

            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, targetUri);

            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("动态路由失败: targetUrl={}", targetUrl, e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
