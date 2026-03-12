package com.api.platform.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FreezeUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reason;

}
