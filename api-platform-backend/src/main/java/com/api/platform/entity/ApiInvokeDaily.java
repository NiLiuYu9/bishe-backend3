package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("api_invoke_daily")
public class ApiInvokeDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("api_id")
    private Long apiId;

    @TableField("api_name")
    private String apiName;

    @TableField("caller_id")
    private Long callerId;

    @TableField("api_owner_id")
    private Long apiOwnerId;

    @TableField("stat_date")
    private LocalDate statDate;

    @TableField("total_count")
    private Long totalCount;

    @TableField("success_count")
    private Long successCount;

    @TableField("fail_count")
    private Long failCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
