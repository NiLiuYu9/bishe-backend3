package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class RequirementDeliverDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "交付网址不能为空")
    @Size(max = 500, message = "交付网址长度不能超过500个字符")
    private String deliveryUrl;

}
