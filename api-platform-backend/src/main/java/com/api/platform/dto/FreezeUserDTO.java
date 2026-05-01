package com.api.platform.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 冻结用户参数
 *
 * 用于管理员冻结用户时提交冻结原因
 */
@Data
public class FreezeUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 冻结原因 */
    private String reason;

}
