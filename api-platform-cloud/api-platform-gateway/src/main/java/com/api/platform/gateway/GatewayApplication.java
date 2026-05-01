package com.api.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关启动类
 *
 * 网关职责：
 * 1. 接收外部API调用请求
 * 2. 执行过滤器链（IP控制→AK/SK鉴权→接口校验→限流→路由→日志）
 * 3. 将请求转发到目标API服务
 * 4. 记录调用日志和统计
 *
 * 通过Nacos注册中心发现后端Dubbo服务
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
