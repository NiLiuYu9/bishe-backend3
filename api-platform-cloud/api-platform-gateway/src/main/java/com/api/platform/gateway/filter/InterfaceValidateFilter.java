package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.api.platform.common.service.InnerInterfaceInfoService;
import com.api.platform.common.service.InnerUserInterfaceInfoService;
import com.api.platform.common.vo.InterfaceInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class InterfaceValidateFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        String method = request.getMethod().name();
        
        InterfaceInfoVO interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        if (interfaceInfo == null) {
            log.warn("接口不存在: {} {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }

        if (!"approved".equals(interfaceInfo.getStatus())) {
            log.warn("接口未审核通过或已下架: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        Long userId = exchange.getAttribute("userId");
        if (userId != null) {
            boolean hasQuota = innerUserInterfaceInfoService.hasQuota(userId, interfaceInfo.getId());
            if (!hasQuota) {
                log.warn("用户配额不足: userId={}, interfaceId={}", userId, interfaceInfo.getId());
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        }

        exchange.getAttributes().put("interfaceId", interfaceInfo.getId());

        if (StrUtil.isNotBlank(interfaceInfo.getTargetUrl())) {
            exchange.getAttributes().put("targetUrl", interfaceInfo.getTargetUrl());
        }

        return chain.filter(exchange);
    }

    private boolean isWhitePath(String path) {
        return path.startsWith("/backend/") ||
               path.startsWith("/auth/") ||
               path.equals("/actuator/health") ||
               path.startsWith("/test/");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
