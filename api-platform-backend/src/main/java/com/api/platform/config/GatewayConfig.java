package com.api.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 网关地址配置类
 * <p>核心职责：从application.yml中读取API网关地址，
 * 供后端在测试调用等场景中构造完整的网关请求URL。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "api.gateway")
public class GatewayConfig {

    /** 网关服务地址（如 http://localhost:8090） */
    private String url;

    /**
     * 获取网关地址
     *
     * @return 网关服务地址
     */
    public String getGatewayUrl() {
        return url;
    }
}
