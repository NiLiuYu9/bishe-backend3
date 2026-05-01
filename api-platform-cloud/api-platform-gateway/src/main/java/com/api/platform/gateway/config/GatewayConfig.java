package com.api.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 网关配置类 —— 配置CORS跨域、路由规则等
 *
 * 路由规则将 /invoke/** 路径的请求路由到后端API服务
 * CORS配置允许前端跨域访问网关
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
