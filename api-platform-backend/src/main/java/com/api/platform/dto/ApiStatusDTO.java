package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ApiStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "状态不能为空")
    private String status;

}
