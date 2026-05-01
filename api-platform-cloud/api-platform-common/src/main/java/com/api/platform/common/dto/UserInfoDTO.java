package com.api.platform.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息DTO —— Dubbo服务间传输的用户信息
 *
 * 网关通过 InnerUserService 查询用户时返回此对象
 * 包含 accessKey、secretKey 等鉴权所需字段
 */
@Data
public class UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String accessKey;
    private String secretKey;
    private Integer status;
}
