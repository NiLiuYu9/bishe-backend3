package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String apiName;

}
