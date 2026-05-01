package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 —— 对应数据库表 order_info
 *
 * 记录用户购买 API 的订单信息，包括购买数量、价格、支付状态、支付流水号等。
 * 订单状态流转：pending(待支付) → paid(已支付) → completed(已完成) / cancelled(已取消)。
 * apiName、buyerName 为冗余存储，避免关联查询。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("order_info")
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 订单编号，唯一，下单时自动生成 */
    @TableField("order_no")
    private String orderNo;

    /** 购买的API ID，关联 api_info.id */
    @TableField("api_id")
    private Long apiId;

    /** API名称，冗余存储，避免关联查询api_info表 */
    @TableField("api_name")
    private String apiName;

    /** 买家用户ID，关联 sys_user.id */
    @TableField("buyer_id")
    private Long buyerId;

    /** 买家用户名，冗余存储，避免关联查询sys_user表 */
    @TableField("buyer_name")
    private String buyerName;

    /** 购买的调用次数配额（-1表示无限次） */
    @TableField("invoke_count")
    private Integer invokeCount;

    /** 订单总金额 */
    @TableField("price")
    private BigDecimal price;

    /** 订单状态：pending(待支付) / paid(已支付) / completed(已完成) / cancelled(已取消) */
    @TableField("status")
    private String status;

    /** 支付平台交易流水号，支付成功后由支付宝回调填入 */
    @TableField("pay_trade_no")
    private String payTradeNo;

    /** 支付方式，如 alipay */
    @TableField("pay_method")
    private String payMethod;

    /** 订单评分，买家对API的评分（0.5-5.0，步长0.5），completed状态时可评价 */
    @TableField("rating")
    private BigDecimal rating;

    /** 支付完成时间 */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /** 订单完成时间 */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0-未删除，1-已删除。@TableLogic 使 delete 操作变为 update */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

}
