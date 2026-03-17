package com.api.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestRecordVO {

    private Long id;

    private Long apiId;

    private String apiName;

    private Object params;

    private Object result;

    private Boolean success;

    private String errorMsg;

    private Integer responseTime;

    private Integer statusCode;

    private LocalDateTime createTime;

}
