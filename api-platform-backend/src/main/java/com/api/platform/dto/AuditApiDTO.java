package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * API审核参数
 *
 * 用于管理员审核API接口，接收审核状态和审核原因
 */
@Data
public class AuditApiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 审核状态，不能为空，可选值：approved（通过）/rejected（拒绝） */
    @NotBlank(message = "审核状态不能为空")
    private String status;

    /** 审核原因，拒绝时需填写拒绝理由 */
    private String reason;

}
