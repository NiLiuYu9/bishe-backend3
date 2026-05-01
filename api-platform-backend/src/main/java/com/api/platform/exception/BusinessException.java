package com.api.platform.exception;

import com.api.platform.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * <p>核心职责：封装业务逻辑中的可预期异常，携带错误码和错误信息，
 * 由GlobalExceptionHandler统一捕获并转换为标准Result响应。
 * 所有业务异常应通过此类抛出，不应使用RuntimeException。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final Integer code;

    /**
     * 仅指定错误信息，使用默认错误码（500）
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
    }

    /**
     * 指定ResultCode，使用其错误码和错误信息
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 指定错误码和错误信息
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 指定ResultCode和自定义错误信息
     * <p>使用ResultCode的错误码，但覆盖其默认信息。</p>
     *
     * @param resultCode 响应码枚举
     * @param message    自定义错误信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

}
