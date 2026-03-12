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
