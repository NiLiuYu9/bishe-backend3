package com.api.platform.common.constant;

/**
 * 鉴权常量 —— 定义AK/SK鉴权相关的请求头名称和常量
 *
 * 网关从请求头中读取这些字段进行签名校验：
 * - ACCESS_KEY：调用者的AccessKey
 * - NONCE：随机字符串，防重复请求
 * - TIMESTAMP：请求时间戳，防重放攻击
 * - SIGN：签名值，SHA256(body + "." + secretKey)
 * - BODY：请求体内容
 */
public class AuthConstants {

    public static final String ACCESS_KEY_HEADER = "accessKey";
    public static final String NONCE_HEADER = "nonce";
    public static final String TIMESTAMP_HEADER = "timestamp";
    public static final String BODY_HEADER = "body";
    public static final String SIGN_HEADER = "sign";
    
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_NAME_HEADER = "X-User-Name";

    private AuthConstants() {
    }
}
