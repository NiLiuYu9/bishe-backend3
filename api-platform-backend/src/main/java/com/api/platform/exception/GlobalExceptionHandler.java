package com.api.platform.exception;

import com.api.platform.common.Result;
import com.api.platform.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>核心职责：统一捕获Controller层抛出的各类异常，转换为标准的Result响应格式。
 * 处理顺序：BusinessException → 参数校验异常 → 参数绑定异常 → 兜底Exception。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * <p>捕获BusinessException，返回对应的错误码和错误信息。</p>
     *
     * @param e 业务异常
     * @return 包含错误码和信息的失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.failed(e.getCode(), e.getMessage());
    }

    /**
     * 处理JSR-303参数校验异常（@Valid/@Validated触发）
     * <p>收集所有字段校验错误信息，以逗号分隔返回。</p>
     *
     * @param e 方法参数校验异常
     * @return 包含校验错误信息的失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常：{}", message);
        return Result.failed(ResultCode.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 处理参数绑定异常（表单提交时类型不匹配等）
     * <p>收集所有字段绑定错误信息，以逗号分隔返回。</p>
     *
     * @param e 绑定异常
     * @return 包含绑定错误信息的失败响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常：{}", message);
        return Result.failed(ResultCode.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 兜底处理所有未捕获异常
     * <p>记录完整异常堆栈，返回通用系统异常提示，避免暴露内部错误细节。</p>
     *
     * @param e 未捕获异常
     * @return 通用系统异常响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.failed("系统异常，请联系管理员");
    }

}
