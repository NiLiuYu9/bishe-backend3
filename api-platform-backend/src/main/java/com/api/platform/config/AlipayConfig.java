package com.api.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱配置类
 * <p>核心职责：从application.yml中读取支付宝沙箱环境配置参数，
 * 包括应用ID、密钥、回调地址等，供支付服务使用。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    /** 支付宝应用ID */
    private String appId;
    /** 应用私钥（RSA2），用于请求签名 */
    private String privateKey;
    /** 支付宝公钥，用于验证支付宝回调签名 */
    private String alipayPublicKey;
    /** 支付宝网关地址（沙箱环境） */
    private String serverUrl;
    /** 支付结果异步通知地址 */
    private String notifyUrl;
    /** 支付完成后同步跳转地址 */
    private String returnUrl;
    /** 字符集，默认UTF-8 */
    private String charset = "UTF-8";
    /** 数据格式，默认json */
    private String format = "json";
    /** 签名算法，默认RSA2（SHA256WithRSA） */
    private String signType = "RSA2";
}
