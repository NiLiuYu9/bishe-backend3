package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("api_info")
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("type_id")
    private Long typeId;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("method")
    private String method;

    @TableField("endpoint")
    private String endpoint;

    @TableField("target_url")
    private String targetUrl;

    @TableField("request_params")
    private String requestParams;

    @TableField("response_params")
    private String responseParams;

    @TableField("price")
    private BigDecimal price;

    @TableField("price_unit")
    private String priceUnit;

    @TableField("call_limit")
    private Integer callLimit;

    @TableField("whitelist_enabled")
    private Integer whitelistEnabled;

    @TableField("status")
    private String status;

    @TableField("doc_url")
    private String docUrl;

    @TableField("rating")
    private BigDecimal rating;

    @TableField("invoke_count")
    private Long invokeCount;

    @TableField("success_count")
    private Long successCount;

    @TableField("fail_count")
    private Long failCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(exist = false)
    private String typeName;

    @TableField(exist = false)
    private String username;

}
