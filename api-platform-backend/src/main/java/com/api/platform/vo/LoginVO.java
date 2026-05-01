package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应数据
 *
 * 登录成功后返回给前端的数据，包含用户基本信息
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 是否管理员（0-普通用户，1-管理员） */
    private Integer isAdmin;

}
