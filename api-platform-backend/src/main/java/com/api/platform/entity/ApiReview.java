package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * API评价实体 —— 对应数据库表 api_review
 *
 * 记录用户对已购买 API 的评价及嵌套回复，支持主评价和两级回复。
 * replyType 标识评价层级：0-主评价 / 1-用户回复 / 2-发布者回复。
 * parentId 指向父评价ID，主评价的 parentId 为 null，回复的 parentId 指向被回复的评价。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_review")
public class ApiReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联订单ID，关联 order_info.id，一个订单只能评价一次 */
    @TableField("order_id")
    private Long orderId;

    /** 关联API ID，关联 api_info.id */
    @TableField("api_id")
    private Long apiId;

    /** 评价用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 评分，1-5分 */
    @TableField("rating")
    private BigDecimal rating;

    /** 评价内容 */
    @TableField("content")
    private String content;

    /** 回复内容（旧版单回复字段，新版使用嵌套回复模式） */
    @TableField("reply")
    private String reply;

    /** 回复时间（旧版单回复时间，新版使用嵌套回复模式） */
    @TableField("reply_time")
    private LocalDateTime replyTime;

    /** 父评价ID，主评价为 null，回复指向被回复的评价ID */
    @TableField("parent_id")
    private Long parentId;

    /** 回复类型：0-主评价 / 1-用户回复（买家追评） / 2-发布者回复（API卖家回应） */
    @TableField("reply_type")
    private Integer replyType;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
