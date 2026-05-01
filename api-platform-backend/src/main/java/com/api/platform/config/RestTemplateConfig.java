package com.api.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * <p>核心职责：注册RestTemplate Bean，用于后端发起HTTP请求调用目标API。
 * 在API转发场景中，后端通过RestTemplate将请求转发到目标服务。</p>
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 注册RestTemplate实例
     *
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
