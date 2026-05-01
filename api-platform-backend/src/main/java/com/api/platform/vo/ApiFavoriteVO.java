package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏响应数据
 *
 * 返回用户收藏的API摘要信息，包括API基本信息和收藏时间
 */
@Data
public class ApiFavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API ID */
    private Long apiId;

    /** API名称 */
    private String apiName;

    /** 所属分类名称 */
    private String typeName;

    /** 请求方法 */
    private String method;

    /** 价格 */
    private BigDecimal price;

    /** 价格单位（次/天/月） */
    private String priceUnit;

    /** 平均评分 */
    private BigDecimal rating;

    /** 总调用次数 */
    private Long invokeCount;

    /** 收藏时间 */
    private LocalDateTime favoriteTime;

}
