package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 申请接单参数
 *
 * 用于开发者申请接单时提交申请说明
 */
@Data
public class RequirementApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请说明，不能为空，最长500个字符 */
    @NotBlank(message = "申请说明不能为空")
    @Size(max = 500, message = "申请说明长度不能超过500个字符")
    private String description;

}
