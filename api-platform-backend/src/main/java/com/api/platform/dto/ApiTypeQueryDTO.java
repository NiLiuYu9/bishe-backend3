package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API分类查询参数
 *
 * 用于API分类列表查询接口，支持按关键词和状态筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiTypeQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    /** 搜索关键词，匹配分类名称 */
    private String keyword;

    /** 分类状态 */
    private String status;

}
