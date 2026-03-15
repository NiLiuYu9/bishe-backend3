package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApiFavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long apiId;

    private String apiName;

    private String typeName;

    private String method;

    private BigDecimal price;

    private String priceUnit;

    private BigDecimal rating;

    private Long invokeCount;

    private LocalDateTime favoriteTime;

}
