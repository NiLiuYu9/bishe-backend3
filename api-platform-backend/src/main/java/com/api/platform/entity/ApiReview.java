package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("api_review")
public class ApiReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("api_id")
    private Long apiId;

    @TableField("user_id")
    private Long userId;

    @TableField("rating")
    private BigDecimal rating;

    @TableField("content")
    private String content;

    @TableField("reply")
    private String reply;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    @TableField("parent_id")
    private Long parentId;

    @TableField("reply_type")
    private Integer replyType;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
