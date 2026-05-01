package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售后信息响应数据
 *
 * 返回需求售后的完整信息，包括申请原因、管理员裁定和售后结果
 */
@Data
public class RequirementAfterSaleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 售后ID */
    private Long id;

    /** 关联需求ID */
    private Long requirementId;

    /** 关联需求标题 */
    private String requirementTitle;

    /** 申请人用户ID */
    private Long applicantId;

    /** 申请人用户名 */
    private String applicantName;

    /** 开发者用户ID */
    private Long developerId;

    /** 开发者用户名 */
    private String developerName;

    /** 售后原因 */
    private String reason;

    /** 未实现的功能说明 */
    private String unimplementedFeatures;

    /** 裁定管理员ID */
    private Long adminId;

    /** 裁定管理员用户名 */
    private String adminName;

    /** 管理员裁定意见 */
    private String adminDecision;

    /** 管理员裁定时间 */
    private LocalDateTime adminDecisionTime;

    /** 售后状态（pending/resolved/rejected） */
    private String status;

    /** 售后结果（completed/refunded） */
    private String result;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

}
