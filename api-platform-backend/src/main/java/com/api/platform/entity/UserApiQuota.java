package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户API配额实体 —— 对应数据库表 user_api_quota
 *
 * 记录用户购买 API 后的调用配额信息，userId + apiId 联合唯一。
 * totalCount 为购买的总次数，usedCount 为已使用次数，remainingCount 为剩余次数。
 * 网关每次成功调用后递增 usedCount，remainingCount 由代码计算维护。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("user_api_quota")
public class UserApiQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID，关联 sys_user.id，与 apiId 联合唯一 */
    @TableField("user_id")
    private Long userId;

    /** API ID，关联 api_info.id，与 userId 联合唯一 */
    @TableField("api_id")
    private Long apiId;

    /** 购买的总调用次数配额 */
    @TableField("total_count")
    private Integer totalCount;

    /** 已使用的调用次数，网关每次成功调用后递增 */
    @TableField("used_count")
    private Integer usedCount;

    /** 剩余调用次数，等于 totalCount - usedCount */
    @TableField("remaining_count")
    private Integer remainingCount;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
