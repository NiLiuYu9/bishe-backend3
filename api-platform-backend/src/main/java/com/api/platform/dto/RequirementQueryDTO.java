package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 需求查询参数
 *
 * 用于需求列表查询接口，支持按关键词、预算范围、状态等条件筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequirementQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    /** 需求发布者用户ID，按发布者筛选 */
    private Long userId;

    /** 搜索关键词，匹配需求标题或描述 */
    private String keyword;

    /** 最低预算，筛选预算不低于此值的需求 */
    private BigDecimal minBudget;

    /** 最高预算，筛选预算不高于此值的需求 */
    private BigDecimal maxBudget;

    /** 需求状态，可选值：open/in_progress/delivered/completed/cancelled */
    private String status;

    /** 排序字段，如budget/createdAt等 */
    private String sortBy;

    /** 排序方向，asc-升序，desc-降序 */
    private String sortOrder;

    /** 申请人用户名，按申请人筛选 */
    private String applicantUsername;

}
