package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    private String username;

    private Integer status;

}
