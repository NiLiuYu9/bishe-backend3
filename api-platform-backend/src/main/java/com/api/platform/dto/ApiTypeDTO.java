package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ApiTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "类型名称不能为空")
    private String name;

    @NotBlank(message = "类型描述不能为空")
    private String description;

}
