package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * API调用排行响应数据
 *
 * 返回API调用排行榜中的单条记录，包含API名称和调用次数
 */
@Data
public class ApiCallRankingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API ID */
    private Long apiId;

    /** API名称 */
    private String apiName;

    /** 调用次数 */
    private Long invokeCount;
}
