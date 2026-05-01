package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 售后裁定参数
 *
 * 用于管理员对售后申请做出裁定决定
 */
@Data
public class AfterSaleDecideDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 裁定决定，不能为空，如resolved（解决）/rejected（驳回） */
    @NotBlank(message = "裁定决定不能为空")
    private String adminDecision;

    /** 裁定结果，如completed（完成）/refunded（退款） */
    private String result;

}
