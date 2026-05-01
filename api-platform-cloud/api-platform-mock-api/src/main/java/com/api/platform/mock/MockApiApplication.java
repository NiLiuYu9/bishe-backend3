package com.api.platform.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Mock API服务1启动类 —— 模拟文本处理、语音处理、图像处理等API
 *
 * 端口：8101，提供3个模拟API接口
 * 网关根据 targetUrl 将请求路由到此服务
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MockApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockApiApplication.class, args);
    }
}
