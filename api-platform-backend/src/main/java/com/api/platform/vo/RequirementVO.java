package com.api.platform.vo;

import com.api.platform.dto.ApiParamDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 需求响应数据
 *
 * 返回需求的完整信息，包括需求描述、预算、申请人列表和交付信息
 */
@Data
public class RequirementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 需求ID */
    private Long id;

    /** 需求发布者用户ID */
    private Long userId;

    /** 需求发布者用户名 */
    private String username;

    /** 需求标题 */
    private String title;

    /** 需求描述 */
    private String description;

    /** 请求参数定义列表 */
    private List<ApiParamDTO> requestParams;

    /** 响应参数定义列表 */
    private List<ApiParamDTO> responseParams;

    /** 预算金额 */
    private BigDecimal budget;

    /** 截止时间 */
    private LocalDateTime deadline;

    /** 需求状态（open/in_progress/delivered/completed/cancelled） */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 申请人列表 */
    private List<ApplicantVO> applicants;

    /** 被选中的开发者信息 */
    private ApplicantVO selectedApplicant;

    /** 当前用户的申请状态 */
    private String myApplyStatus;

    /** 交付物地址 */
    private String deliveryUrl;

    /** 技术标签列表 */
    private List<String> tags;

    /** 匹配评分（智能推荐用） */
    private Double matchScore;

}
