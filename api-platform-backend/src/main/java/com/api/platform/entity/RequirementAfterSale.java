package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("requirement_after_sale")
public class RequirementAfterSale implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("requirement_id")
    private Long requirementId;

    @TableField("applicant_id")
    private Long applicantId;

    @TableField("developer_id")
    private Long developerId;

    @TableField("reason")
    private String reason;

    @TableField("unimplemented_features")
    private String unimplementedFeatures;

    @TableField("developer_response")
    private String developerResponse;

    @TableField("developer_response_time")
    private LocalDateTime developerResponseTime;

    @TableField("admin_id")
    private Long adminId;

    @TableField("admin_decision")
    private String adminDecision;

    @TableField("admin_decision_time")
    private LocalDateTime adminDecisionTime;

    @TableField("status")
    private String status;

    @TableField("result")
    private String result;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String requirementTitle;

    @TableField(exist = false)
    private String applicantName;

    @TableField(exist = false)
    private String developerName;

    @TableField(exist = false)
    private String adminName;

}
