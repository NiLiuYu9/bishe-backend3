package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户技能标签实体 —— 对应数据库表 user_tag
 *
 * 记录用户的技能标签（如 Java、Python、前端开发等），用于智能匹配推荐。
 * 一个用户可关联多个标签，与 sys_user 表为一对多关系。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("user_tag")
public class UserTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 标签名称，如 Java、Python、前端开发、微服务等 */
    @TableField("tag_name")
    private String tagName;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
