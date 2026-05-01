package com.api.platform.dto;

import lombok.Data;

import java.util.Map;

/**
 * API调用请求参数
 *
 * 用于 /invoke 接口，接收API在线调用的必要参数，包括目标API标识、鉴权凭证和请求参数
 */
@Data
public class ApiInvokeDTO {

    /** 目标API的ID */
    private Long apiId;

    /** 访问密钥（AK），用于调用鉴权 */
    private String accessKey;

    /** 密钥（SK），用于调用签名 */
    private String secretKey;

    /** 请求参数键值对 */
    private Map<String, Object> params;

}
