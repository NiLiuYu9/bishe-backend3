package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequirementQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String keyword;

    private BigDecimal minBudget;

    private BigDecimal maxBudget;

    private String status;

    private String sortBy;

    private String sortOrder;

    private String applicantUsername;

}
