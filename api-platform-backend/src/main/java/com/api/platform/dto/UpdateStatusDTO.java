package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 状态更新参数
 *
 * 用于通用的状态变更操作，接收目标状态值
 */
@Data
public class UpdateStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态，不能为空 */
    @NotBlank(message = "状态不能为空")
    private String status;

}
