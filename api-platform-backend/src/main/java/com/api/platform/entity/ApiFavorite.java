package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API收藏实体 —— 对应数据库表 api_favorite
 *
 * 记录用户收藏的 API，userId + apiId 联合唯一，同一用户不可重复收藏同一 API。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_favorite")
public class ApiFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 收藏用户ID，关联 sys_user.id，与 apiId 联合唯一 */
    @TableField("user_id")
    private Long userId;

    /** 收藏的API ID，关联 api_info.id，与 userId 联合唯一 */
    @TableField("api_id")
    private Long apiId;

    /** 收藏时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
