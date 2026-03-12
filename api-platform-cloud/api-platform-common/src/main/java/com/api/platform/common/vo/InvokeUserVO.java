package com.api.platform.common.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokeUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String accessKey;

    private String secretKey;

    private Integer status;
}
