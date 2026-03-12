package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("api_test_record")
public class ApiTestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("api_id")
    private Long apiId;

    @TableField("api_name")
    private String apiName;

    @TableField("user_id")
    private Long userId;

    @TableField("params")
    private String params;

    @TableField("result")
    private String result;

    @TableField("success")
    private Integer success;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("response_time")
    private Integer responseTime;

    @TableField("status_code")
    private Integer statusCode;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
