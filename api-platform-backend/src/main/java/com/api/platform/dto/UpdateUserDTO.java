package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 更新用户信息请求参数
 *
 * 用于 /auth/updateUser 接口，接收用户信息修改表单数据
 */
@Data
public class UpdateUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    private String username;

    /** 邮箱，需符合标准邮箱格式 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号，可为空或符合中国大陆手机号格式 */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 是否管理员，0-普通用户，1-管理员 */
    private Integer isAdmin;

}
