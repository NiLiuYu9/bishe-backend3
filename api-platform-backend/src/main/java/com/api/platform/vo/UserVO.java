package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer isAdmin;
    private Integer status;
    private String freezeReason;
    private LocalDateTime createTime;
    private List<String> tags;

}
