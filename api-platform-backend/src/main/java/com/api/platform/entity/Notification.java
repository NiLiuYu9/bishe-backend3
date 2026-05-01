package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息实体 —— 对应数据库表 notification_message
 *
 * 记录平台站内通知消息，如订单支付成功、需求状态变更、售后进展等。
 * type 标识通知类型，relatedId + relatedType 关联到具体业务对象（如订单、需求、售后）。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("notification_message")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 通知接收用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 通知类型，如 order(订单通知) / requirement(需求通知) / after_sale(售后通知) / system(系统通知) 等 */
    @TableField("type")
    private String type;

    /** 通知标题 */
    @TableField("title")
    private String title;

    /** 通知内容 */
    @TableField("content")
    private String content;

    /** 关联业务对象ID，如订单ID、需求ID、售后ID等 */
    @TableField("related_id")
    private Long relatedId;

    /** 关联业务对象类型，如 order / requirement / after_sale 等，与 relatedId 配合定位具体业务 */
    @TableField("related_type")
    private String relatedType;

    /** 是否已读：0-未读，1-已读 */
    @TableField("is_read")
    private Integer isRead;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
