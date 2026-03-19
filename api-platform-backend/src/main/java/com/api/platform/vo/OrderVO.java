package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderNo;

    private Long apiId;

    private String apiName;

    private Long buyerId;

    private String buyerName;

    private Integer invokeCount;

    private BigDecimal price;

    private String status;

    private BigDecimal rating;

    private String reviewContent;

    private Long reviewId;

    private String createTime;

    private String payTime;

    private String completeTime;
}
