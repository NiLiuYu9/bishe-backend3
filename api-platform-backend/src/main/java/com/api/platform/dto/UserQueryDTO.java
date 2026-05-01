package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 *
 * 用于管理后台用户列表查询接口，支持按用户名和状态筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    /** 用户名，模糊匹配 */
    private String username;

    /** 用户状态，0-正常，1-冻结 */
    private Integer status;

}
