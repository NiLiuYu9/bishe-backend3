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

/**
 * 接口校验过滤器 —— 网关过滤器链第3环（Order=1）
 *
 * 职责：校验请求的接口是否存在、是否已审核通过、用户是否有调用配额
 * 校验流程：
 * 1. 根据请求路径+方法查询接口信息（通过Dubbo调用后端服务）
 * 2. 检查接口是否存在
 * 3. 检查接口状态是否为 approved（已审核通过）
 * 4. 检查用户是否还有剩余调用配额
 * 5. 将接口信息存入请求属性，供后续过滤器使用
 */
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

        if (interfaceInfo.getCallLimit() != null && interfaceInfo.getCallLimit() > 0) {
            exchange.getAttributes().put("callLimit", interfaceInfo.getCallLimit());
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
