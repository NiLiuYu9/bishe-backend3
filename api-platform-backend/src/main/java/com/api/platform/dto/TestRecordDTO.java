package com.api.platform.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TestRecordDTO {

    private Long apiId;

    private String apiName;

    private Map<String, Object> params;

    private Object result;

    private Boolean success;

    private String errorMsg;

    private Integer responseTime;

    private Integer statusCode;

}
