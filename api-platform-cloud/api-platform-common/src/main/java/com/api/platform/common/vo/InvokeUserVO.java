package com.api.platform.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用用户VO —— Dubbo服务间传输的调用者信息
 *
 * 网关通过 InnerUserService 查询用户鉴权信息时返回此对象
 * 包含用户ID、accessKey、secretKey、状态等字段
 */
@Data
public class InvokeUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String accessKey;

    private String secretKey;

    private Integer status;
}
