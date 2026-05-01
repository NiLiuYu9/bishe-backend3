package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 交付需求参数
 *
 * 用于开发者交付需求时提交交付链接
 */
@Data
public class RequirementDeliverDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 交付网址，不能为空，最长500个字符 */
    @NotBlank(message = "交付网址不能为空")
    @Size(max = 500, message = "交付网址长度不能超过500个字符")
    private String deliveryUrl;

}
