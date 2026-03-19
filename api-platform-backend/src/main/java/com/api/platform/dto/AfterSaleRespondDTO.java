package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class AfterSaleRespondDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "回应内容不能为空")
    private String developerResponse;

}
