package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 需求实体 —— 对应数据库表 requirement
 *
 * 记录需求方发布的定制化 API 开发需求，包括预算、截止日期、状态流转等。
 * 需求状态流转：open(开放中) → in_progress(开发中) → delivered(已交付) → completed(已完成) / cancelled(已取消)。
 * tags 为非持久化字段，通过 requirement_tag 关联表查询填充。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("requirement")
public class Requirement implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 需求发布者用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 需求标题 */
    @TableField("title")
    private String title;

    /** 需求详细描述 */
    @TableField("description")
    private String description;

    /** 期望的请求参数定义，JSON格式存储 */
    @TableField("request_params")
    private String requestParams;

    /** 期望的响应参数定义，JSON格式存储 */
    @TableField("response_params")
    private String responseParams;

    /** 预算金额 */
    @TableField("budget")
    private BigDecimal budget;

    /** 截止日期 */
    @TableField("deadline")
    private LocalDateTime deadline;

    /** 需求状态：open(开放中) / in_progress(开发中) / delivered(已交付) / completed(已完成) / cancelled(已取消) */
    @TableField("status")
    private String status;

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

    /** 交付URL，开发者交付时填入的API访问地址 */
    @TableField("delivery_url")
    private String deliveryUrl;

    /** 发布者用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String username;

    /** 需求技术标签列表，非数据库字段，通过 requirement_tag 关联表查询填充 */
    @TableField(exist = false)
    private List<String> tags;

}
