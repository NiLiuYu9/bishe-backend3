package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * API分类创建参数
 *
 * 用于创建或更新API分类，接收分类名称和描述
 */
@Data
public class ApiTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类名称，不能为空 */
    @NotBlank(message = "类型名称不能为空")
    private String name;

    /** 分类描述，不能为空 */
    @NotBlank(message = "类型描述不能为空")
    private String description;

}
