package com.api.platform.common.constant;

/**
 * 服务常量 —— 定义Dubbo服务相关的常量
 *
 * 包括Dubbo服务版本号、超时时间、重试次数等配置
 */
public class ServiceConstants {

    public static final String MAIN_SERVICE = "api-platform-backend";
    public static final String MOCK_API_SERVICE = "api-platform-mock-api";
    public static final String GATEWAY_SERVICE = "api-platform-gateway";

    private ServiceConstants() {
    }
}
