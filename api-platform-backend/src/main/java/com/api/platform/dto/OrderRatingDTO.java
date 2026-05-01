package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单评分参数
 *
 * 用于订单完成后的评分操作，评分范围为0.5-5.0
 */
@Data
public class OrderRatingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评分，不能为空，最低0.5，最高5.0 */
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "0.5", message = "评分最低为0.5")
    @DecimalMax(value = "5.0", message = "评分最高为5.0")
    private BigDecimal rating;

}
