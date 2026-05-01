package com.api.platform.dto;

import lombok.Data;

import java.util.Map;

/**
 * 测试记录参数
 *
 * 用于API在线测试时记录测试请求和响应信息
 */
@Data
public class TestRecordDTO {

    /** 被测试的API ID */
    private Long apiId;

    /** API名称 */
    private String apiName;

    /** 测试请求参数键值对 */
    private Map<String, Object> params;

    /** 测试响应结果 */
    private Object result;

    /** 测试是否成功 */
    private Boolean success;

    /** 错误信息，测试失败时记录 */
    private String errorMsg;

    /** 响应时间（毫秒） */
    private Integer responseTime;

    /** HTTP状态码 */
    private Integer statusCode;

}
