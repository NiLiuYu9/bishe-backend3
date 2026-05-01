package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 售后消息参数
 *
 * 用于售后对话中发送消息
 */
@Data
public class AfterSaleMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息内容，不能为空 */
    @NotBlank(message = "消息内容不能为空")
    private String content;

}
