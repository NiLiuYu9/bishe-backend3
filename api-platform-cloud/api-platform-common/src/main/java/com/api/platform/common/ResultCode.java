package com.api.platform.common;

import lombok.Getter;

/**
 * 响应码枚举 —— 定义常用的业务状态码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    ACCESS_KEY_INVALID(1003, "AccessKey无效"),
    SECRET_KEY_INVALID(1004, "SecretKey无效"),
    AK_SK_REQUIRED(1005, "AccessKey和SecretKey不能为空"),
    QUOTA_EXCEEDED(1006, "配额已用尽");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
