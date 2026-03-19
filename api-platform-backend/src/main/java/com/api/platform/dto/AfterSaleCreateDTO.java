package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AfterSaleCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "需求ID不能为空")
    private Long requirementId;

    @NotBlank(message = "售后原因不能为空")
    private String reason;

    private String unimplementedFeatures;

}
