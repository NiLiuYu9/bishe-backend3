package com.api.platform.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mock API服务2启动类 —— 模拟地图服务、短信服务、支付服务、数据服务等API
 *
 * 端口：8102，提供4个模拟API接口
 * 网关根据 targetUrl 将请求路由到此服务
 */
@SpringBootApplication
public class MockApi2Application {

    public static void main(String[] args) {
        SpringApplication.run(MockApi2Application.class, args);
    }
}
