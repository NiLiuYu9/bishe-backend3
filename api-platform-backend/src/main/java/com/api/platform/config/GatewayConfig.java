package com.api.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "api.gateway")
public class GatewayConfig {

    private String url;

    public String getGatewayUrl() {
        return url;
    }
}
