package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * API参数定义
 *
 * 用于定义API的请求参数或响应参数结构，包含参数名、类型、是否必填、描述和示例值
 */
@Data
public class ApiParamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 参数名，不能为空 */
    @NotBlank(message = "参数名不能为空")
    private String name;

    /** 参数类型（如String、Integer、Boolean等），不能为空 */
    @NotBlank(message = "参数类型不能为空")
    private String type;

    /** 是否必填，true-必填，false-选填 */
    private Boolean required;

    /** 参数描述说明 */
    private String description;

    /** 参数示例值 */
    private String example;

}
