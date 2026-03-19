package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RequirementAfterSaleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long requirementId;

    private String requirementTitle;

    private Long applicantId;

    private String applicantName;

    private Long developerId;

    private String developerName;

    private String reason;

    private String unimplementedFeatures;

    private String developerResponse;

    private LocalDateTime developerResponseTime;

    private Long adminId;

    private String adminName;

    private String adminDecision;

    private LocalDateTime adminDecisionTime;

    private String status;

    private String result;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
