package com.api.platform.common;

import lombok.Getter;

/**
 * 响应码枚举
 * <p>核心职责：定义系统中所有API响应的状态码和对应消息，
 * 统一管理错误码规范，前端根据code值进行不同的业务处理。</p>
 */
@Getter
public enum ResultCode {

    /** 操作成功 */
    SUCCESS(200, "操作成功"),
    /** 操作失败（通用） */
    FAILED(500, "操作失败"),
    /** 参数校验失败 */
    VALIDATE_FAILED(400, "参数校验失败"),
    /** 未登录或Token已过期 */
    UNAUTHORIZED(401, "未登录或token已过期"),
    /** 没有相关权限 */
    FORBIDDEN(403, "没有相关权限"),
    /** 用户不存在 */
    USER_NOT_FOUND(1001, "用户不存在"),
    /** 密码错误 */
    PASSWORD_ERROR(1002, "密码错误"),
    /** 用户名已存在 */
    USER_EXISTS(1003, "用户名已存在"),
    /** Token无效 */
    TOKEN_INVALID(1004, "Token无效"),
    /** Token已过期 */
    TOKEN_EXPIRED(1005, "Token已过期"),
    /** 用户已被禁用 */
    USER_DISABLED(1006, "用户已被禁用"),
    /** API类型名称已存在 */
    API_TYPE_EXISTS(2001, "类型名称已存在"),
    /** AccessKey和SecretKey不能为空 */
    AK_SK_REQUIRED(1007, "AccessKey和SecretKey不能为空");

    /** 状态码 */
    private final Integer code;
    /** 状态消息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
