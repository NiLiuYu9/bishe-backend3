package com.api.platform.vo;

import lombok.Data;

import java.util.Map;

/**
 * API调用结果响应数据
 *
 * 返回API调用的执行结果，包括响应数据、状态码和耗时
 */
@Data
public class ApiInvokeResultVO {

    /** 调用是否成功 */
    private Boolean success;

    /** 结果消息 */
    private String message;

    /** API名称 */
    private String apiName;

    /** API请求路径 */
    private String endpoint;

    /** 请求方法 */
    private String method;

    /** 请求参数 */
    private Map<String, Object> requestParams;

    /** 响应结果数据 */
    private Object result;

    /** 响应耗时（毫秒） */
    private Long responseTime;

    /** HTTP状态码 */
    private Integer statusCode;

}
