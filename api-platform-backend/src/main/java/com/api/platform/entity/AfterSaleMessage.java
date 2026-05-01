package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售后对话记录实体 —— 对应数据库表 after_sale_message
 *
 * 记录售后工单中的对话消息，支持需求方、开发者、管理员三方沟通。
 * senderType 标识发送者角色：applicant(需求方) / developer(开发者) / admin(管理员)。
 * senderName 为非持久化字段，查询时关联填充。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("after_sale_message")
public class AfterSaleMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联售后工单ID，关联 requirement_after_sale.id */
    @TableField("after_sale_id")
    private Long afterSaleId;

    /** 消息发送者用户ID，关联 sys_user.id */
    @TableField("sender_id")
    private Long senderId;

    /** 发送者角色类型：applicant(需求方) / developer(开发者) / admin(管理员) */
    @TableField("sender_type")
    private String senderType;

    /** 消息内容 */
    @TableField("content")
    private String content;

    /** 消息发送时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 发送者用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String senderName;

}
