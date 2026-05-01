package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建评价参数
 *
 * 用于用户对已完成的订单提交API评价，包含评分和评论内容
 */
@Data
public class ApiReviewCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID，不能为空，指定评价关联的订单 */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 评分，不能为空，最低0.5，最高5.0 */
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "0.5", message = "评分最低为0.5")
    @DecimalMax(value = "5.0", message = "评分最高为5.0")
    private BigDecimal rating;

    /** 评价内容 */
    private String content;

}
