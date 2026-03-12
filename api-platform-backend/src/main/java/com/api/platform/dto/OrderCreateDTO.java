package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "API ID不能为空")
    private Long apiId;

    @NotNull(message = "调用次数不能为空")
    @Min(value = 1, message = "调用次数最小为1")
    private Integer invokeCount;

}
