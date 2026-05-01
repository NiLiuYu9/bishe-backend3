package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 选择开发者参数
 *
 * 用于需求方从申请人中选择一位开发者接单
 */
@Data
public class RequirementApplicantSelectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 被选中的申请者用户ID，不能为空 */
    @NotNull(message = "申请者ID不能为空")
    private Long applicantId;

}
