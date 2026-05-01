package com.api.platform.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息传输对象
 *
 * 用于在内部服务间传递用户基本信息，包含用户ID、凭证和状态
 */
@Data
public class UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 访问密钥（AK），用于API调用鉴权 */
    private String accessKey;
    /** 密钥（SK），用于API调用签名 */
    private String secretKey;
    /** 用户状态，0-正常，1-冻结 */
    private Integer status;
}
