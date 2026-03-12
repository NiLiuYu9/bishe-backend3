package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiCallRankingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long apiId;

    private String apiName;

    private Long invokeCount;
}
