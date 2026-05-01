package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API白名单实体 —— 对应数据库表 api_whitelist
 *
 * 记录 API 的白名单用户，当 API 启用白名单模式时，仅白名单中的用户可调用该 API。
 * 一个 API 可添加多个白名单用户，由 API 发布者自行管理。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_whitelist")
public class ApiWhitelist implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** API ID，关联 api_info.id */
    @TableField("api_id")
    private Long apiId;

    /** 白名单用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
