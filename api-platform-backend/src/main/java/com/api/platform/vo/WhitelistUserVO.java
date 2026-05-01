package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 白名单用户响应数据
 *
 * 返回API白名单中的用户信息
 */
@Data
public class WhitelistUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 白名单记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 加入白名单时间 */
    private LocalDateTime createTime;

}
