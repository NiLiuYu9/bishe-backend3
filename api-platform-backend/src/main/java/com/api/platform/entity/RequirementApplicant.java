package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("requirement_applicant")
public class RequirementApplicant implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("requirement_id")
    private Long requirementId;

    @TableField("user_id")
    private Long userId;

    @TableField("description")
    private String description;

    @TableField("status")
    private String status;

    @TableField(value = "apply_time", fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    @TableField(exist = false)
    private String username;

}
