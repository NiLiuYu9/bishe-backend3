package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建售后申请参数
 *
 * 用于提交售后申请，需指定关联需求ID和售后原因
 */
@Data
public class AfterSaleCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 需求ID，不能为空，指定关联的需求 */
    @NotNull(message = "需求ID不能为空")
    private Long requirementId;

    /** 售后原因，不能为空 */
    @NotBlank(message = "售后原因不能为空")
    private String reason;

    /** 未实现的功能描述 */
    private String unimplementedFeatures;

}
