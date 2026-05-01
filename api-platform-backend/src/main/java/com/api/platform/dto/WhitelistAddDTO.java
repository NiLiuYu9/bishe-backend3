package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 白名单添加参数
 *
 * 用于将指定用户添加到API白名单，允许免费调用
 */
@Data
public class WhitelistAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名列表，不能为空，批量添加白名单用户 */
    @NotEmpty(message = "用户名列表不能为空")
    private List<String> usernames;

}
