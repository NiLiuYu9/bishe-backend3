package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private Integer isAdmin;

}
