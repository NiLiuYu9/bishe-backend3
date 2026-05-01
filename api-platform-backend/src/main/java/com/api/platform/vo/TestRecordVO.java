package com.api.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试记录响应数据
 *
 * 返回API在线测试的记录信息，包括请求参数、响应结果和执行状态
 */
@Data
public class TestRecordVO {

    /** 记录ID */
    private Long id;

    /** 关联API ID */
    private Long apiId;

    /** API名称 */
    private String apiName;

    /** 请求参数 */
    private Object params;

    /** 响应结果 */
    private Object result;

    /** 测试是否成功 */
    private Boolean success;

    /** 错误信息 */
    private String errorMsg;

    /** 响应耗时（毫秒） */
    private Integer responseTime;

    /** HTTP状态码 */
    private Integer statusCode;

    /** 创建时间 */
    private LocalDateTime createTime;

}
