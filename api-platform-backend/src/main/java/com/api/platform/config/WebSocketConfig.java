package com.api.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 * <p>核心职责：注册ServerEndpointExporter Bean，
 * 使Spring容器能够自动发现和部署@ServerEndpoint注解的WebSocket端点。</p>
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注册WebSocket服务端点导出器
     * <p>Spring Boot默认不自动扫描@ServerEndpoint，需要此Bean才能使WebSocket端点生效。</p>
     *
     * @return ServerEndpointExporter实例
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
