package com.api.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "api.gateway")
public class GatewayConfig {

    private String url;

    private String mockPath = "/mock";

    public String getMockApiUrl(String endpoint) {
        return url + mockPath + endpoint;
    }

    public String getApiUrl(String endpoint) {
        return url + endpoint;
    }
}
