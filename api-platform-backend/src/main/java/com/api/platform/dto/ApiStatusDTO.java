package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * API状态变更参数
 *
 * 用于API上下架操作，接收目标状态值
 */
@Data
public class ApiStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态，不能为空，可选值：offline（下架） */
    @NotBlank(message = "状态不能为空")
    private String status;

}
