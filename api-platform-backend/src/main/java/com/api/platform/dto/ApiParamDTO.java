package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ApiParamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数名不能为空")
    private String name;

    @NotBlank(message = "参数类型不能为空")
    private String type;

    private Boolean required;

    private String description;

    private String example;

}
