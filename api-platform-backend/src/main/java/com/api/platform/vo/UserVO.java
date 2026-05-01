package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息响应数据
 *
 * 返回用户的详细信息，包括基本资料、账号状态和技能标签
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 是否管理员（0-普通用户，1-管理员） */
    private Integer isAdmin;

    /** 账号状态（0-正常，1-冻结） */
    private Integer status;

    /** 冻结原因 */
    private String freezeReason;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 用户技能标签列表 */
    private List<String> tags;

}
