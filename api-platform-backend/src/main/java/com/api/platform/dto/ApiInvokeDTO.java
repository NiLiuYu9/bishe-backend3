package com.api.platform.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ApiInvokeDTO {

    private Long apiId;

    private String accessKey;

    private String secretKey;

    private Map<String, Object> params;

}
