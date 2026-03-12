package com.api.platform.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ApiInvokeResultVO {

    private Boolean success;

    private String message;

    private String apiName;

    private String endpoint;

    private String method;

    private Map<String, Object> requestParams;

    private Object result;

}
