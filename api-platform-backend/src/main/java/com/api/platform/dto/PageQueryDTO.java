package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页最多查询100条")
    private Integer pageSize = 10;

}
