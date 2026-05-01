package com.api.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * API开放平台启动类
 * <p>核心职责：Spring Boot应用入口，启用以下功能：
 * - MapperScan：扫描Mapper接口，注册MyBatis代理Bean
 * - EnableDiscoveryClient：注册到Nacos服务发现
 * - EnableScheduling：启用定时任务调度</p>
 */
@SpringBootApplication
@MapperScan("com.api.platform.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class ApiPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiPlatformApplication.class, args);
    }

}
