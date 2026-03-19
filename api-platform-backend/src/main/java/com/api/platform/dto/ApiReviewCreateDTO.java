package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ApiReviewCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "0.5", message = "评分最低为0.5")
    @DecimalMax(value = "5.0", message = "评分最高为5.0")
    private BigDecimal rating;

    private String content;

}
