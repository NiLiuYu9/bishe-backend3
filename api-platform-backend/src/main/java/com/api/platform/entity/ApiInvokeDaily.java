package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日调用统计实体 —— 对应数据库表 api_invoke_daily
 *
 * 记录 API 每日的调用统计数据，按 apiId + callerId + statDate 联合唯一。
 * 数据由网关 ResponseLogFilter 实时累加到 Redis，再由定时任务同步到 MySQL。
 * apiName 为冗余存储，避免关联查询。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_invoke_daily")
public class ApiInvokeDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** API ID，关联 api_info.id */
    @TableField("api_id")
    private Long apiId;

    /** API名称，冗余存储，避免关联查询api_info表 */
    @TableField("api_name")
    private String apiName;

    /** 调用者用户ID，关联 sys_user.id */
    @TableField("caller_id")
    private Long callerId;

    /** API所有者用户ID，关联 sys_user.id，冗余存储便于按发布者统计 */
    @TableField("api_owner_id")
    private Long apiOwnerId;

    /** 统计日期，与 apiId + callerId 联合唯一 */
    @TableField("stat_date")
    private LocalDate statDate;

    /** 当日总调用次数 */
    @TableField("total_count")
    private Long totalCount;

    /** 当日成功调用次数 */
    @TableField("success_count")
    private Long successCount;

    /** 当日失败调用次数 */
    @TableField("fail_count")
    private Long failCount;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
