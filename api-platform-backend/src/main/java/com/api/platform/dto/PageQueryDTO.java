package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询基础参数
 *
 * 所有分页查询DTO的基类，提供统一的页码和每页条数参数
 */
@Data
public class PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，最小为1，默认1 */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /** 每页条数，最小1，最大100，默认10 */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页最多查询100条")
    private Integer pageSize = 10;

}
