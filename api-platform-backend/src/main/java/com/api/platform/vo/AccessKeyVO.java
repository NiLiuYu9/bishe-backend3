package com.api.platform.vo;

import lombok.Data;

/**
 * 密钥响应数据
 *
 * 返回用户的访问密钥信息，用于API调用时的AK/SK鉴权
 */
@Data
public class AccessKeyVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 访问密钥（Access Key），用于标识调用者身份 */
    private String accessKey;

    /** 密钥（Secret Key），用于生成请求签名，需妥善保管 */
    private String secretKey;

}
