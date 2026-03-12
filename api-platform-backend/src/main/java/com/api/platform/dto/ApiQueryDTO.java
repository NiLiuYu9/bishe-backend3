package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String keyword;

    private Long typeId;

    private String status;

    private String sortBy;

    private String sortOrder;

    private String authorName;

}
