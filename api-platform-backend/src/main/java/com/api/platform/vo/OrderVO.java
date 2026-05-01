package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单响应数据
 *
 * 返回订单的完整信息，包括订单基本信息、关联API、购买者、支付状态和评价信息
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 关联API ID */
    private Long apiId;

    /** 关联API名称 */
    private String apiName;

    /** 购买者用户ID */
    private Long buyerId;

    /** 购买者用户名 */
    private String buyerName;

    /** 购买的调用次数配额（-1表示无限次） */
    private Integer invokeCount;

    /** 订单价格 */
    private BigDecimal price;

    /** 订单状态（pending=待支付, paid=已支付, completed=已完成, refunded=已退款, cancelled=已取消） */
    private String status;

    /** 评价评分 */
    private BigDecimal rating;

    /** 评价内容，关联api_review表，展示该订单的最新评价信息 */
    private String reviewContent;

    /** 评价ID，关联api_review表，展示该订单的最新评价信息 */
    private Long reviewId;

    /** 创建时间（String类型：ServiceImpl中已格式化为yyyy-MM-dd HH:mm:ss，前端直接展示） */
    private String createTime;

    /** 支付时间（String类型：ServiceImpl中已格式化为yyyy-MM-dd HH:mm:ss，前端直接展示） */
    private String payTime;

    /** 完成时间（String类型：ServiceImpl中已格式化为yyyy-MM-dd HH:mm:ss，前端直接展示） */
    private String completeTime;
}
