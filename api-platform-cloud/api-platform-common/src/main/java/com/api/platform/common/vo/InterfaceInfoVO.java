package com.api.platform.common.vo;

import lombok.Data;

import java.io.Serializable;

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
