package com.api.platform.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mock API服务3启动类 —— 模拟翻译服务、OCR服务、人脸识别等API
 *
 * 端口：8103，提供3个模拟API接口
 * 网关根据 targetUrl 将请求路由到此服务
 */
@SpringBootApplication
public class MockApi3Application {

    public static void main(String[] args) {
        SpringApplication.run(MockApi3Application.class, args);
    }
}
