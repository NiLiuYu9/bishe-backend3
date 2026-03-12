package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RequirementApplicantSelectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "申请者ID不能为空")
    private Long applicantId;

}
