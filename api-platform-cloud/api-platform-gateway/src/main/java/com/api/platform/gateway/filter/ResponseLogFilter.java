package com.api.platform.gateway.filter;

import com.api.platform.common.service.InnerUserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 响应日志过滤器 —— 网关过滤器链第4环（Order=2）
 *
 * 职责：记录API调用的响应日志，并通过Dubbo更新调用次数统计
 * 在请求完成后：
 * 1. 记录响应状态码、耗时等信息
 * 2. 通过Dubbo调用后端服务，更新接口调用次数（Redis+MySQL）
 * 3. 更新用户调用配额的已使用次数
 */
@Slf4j
@Component
public class ResponseLogFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Long userId = exchange.getAttribute("userId");
                Long interfaceId = exchange.getAttribute("interfaceId");
                
                if (userId != null && interfaceId != null) {
                    try {
                        innerUserInterfaceInfoService.invokeCount(interfaceId, userId);
                        log.info("调用次数更新成功: userId={}, interfaceId={}", userId, interfaceId);
                    } catch (Exception e) {
                        log.error("更新调用次数失败: {}", e.getMessage());
                    }
                }
            }
        }));
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
