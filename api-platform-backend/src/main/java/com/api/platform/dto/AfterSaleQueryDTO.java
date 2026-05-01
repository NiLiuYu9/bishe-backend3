package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 售后查询参数
 *
 * 用于售后列表查询接口，支持按需求ID、申请人、开发者、状态筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AfterSaleQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 需求ID，按关联需求筛选 */
    private Long requirementId;

    /** 申请人用户ID，按申请人筛选 */
    private Long applicantId;

    /** 开发者用户ID，按开发者筛选 */
    private Long developerId;

    /** 售后状态，可选值：pending/resolved/rejected */
    private String status;

}
