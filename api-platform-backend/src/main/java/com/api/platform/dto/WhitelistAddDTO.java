package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class WhitelistAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "用户名列表不能为空")
    private List<String> usernames;

}
