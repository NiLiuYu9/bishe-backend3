package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.api.platform.common.constant.AuthConstants;
import com.api.platform.common.service.InnerUserService;
import com.api.platform.common.service.InnerUserInterfaceInfoService;
import com.api.platform.common.utils.SignUtils;
import com.api.platform.common.vo.InvokeUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();

        String accessKey = headers.getFirst(AuthConstants.ACCESS_KEY_HEADER);
        String nonce = headers.getFirst(AuthConstants.NONCE_HEADER);
        String timestamp = headers.getFirst(AuthConstants.TIMESTAMP_HEADER);
        String sign = headers.getFirst(AuthConstants.SIGN_HEADER);
        String body = headers.getFirst(AuthConstants.BODY_HEADER);

        if (StrUtil.isBlank(accessKey)) {
            return handleNoAuth(exchange, "accessKey不能为空");
        }

        InvokeUserVO user = innerUserService.getInvokeUser(accessKey);
        if (user == null) {
            return handleNoAuth(exchange, "用户不存在");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            return handleNoAuth(exchange, "用户已被禁用");
        }

        if (StrUtil.isBlank(nonce) || Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(exchange, "nonce无效");
        }

        if (StrUtil.isBlank(timestamp)) {
            return handleNoAuth(exchange, "timestamp不能为空");
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if ((currentTime - Long.parseLong(timestamp)) >= 300L) {
            return handleNoAuth(exchange, "请求已过期");
        }

        if (StrUtil.isBlank(sign)) {
            return handleNoAuth(exchange, "sign不能为空");
        }
        String serverSign = SignUtils.genSign(body == null ? "" : body, user.getSecretKey());
        if (!sign.equals(serverSign)) {
            return handleNoAuth(exchange, "签名验证失败");
        }

        exchange.getAttributes().put("userId", user.getId());
        exchange.getAttributes().put("accessKey", accessKey);

        return chain.filter(exchange);
    }

    private boolean isWhitePath(String path) {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/actuator/health") ||
               path.contains("/test/");
    }

    private Mono<Void> handleNoAuth(ServerWebExchange exchange, String message) {
        log.warn("鉴权失败: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
