package com.api.platform.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息VO —— Dubbo服务间传输的接口信息
 *
 * 网关通过 InnerInterfaceInfoService 查询接口时返回此对象
 * 包含接口状态、目标URL、调用限制等路由和校验所需字段
 */
@Data
public class InterfaceInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String path;

    private String method;

    private String status;

    private String targetUrl;

    private Integer callLimit;
}
