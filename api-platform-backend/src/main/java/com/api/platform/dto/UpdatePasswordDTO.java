package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改密码请求参数
 *
 * 用于 /auth/updatePassword 接口，接收用户修改密码表单数据
 */
@Data
public class UpdatePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 原密码，不能为空 */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码，6-20个字符 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20位之间")
    private String newPassword;

}
