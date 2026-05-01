package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API查询参数
 *
 * 用于API列表查询接口，支持按关键词、分类、状态等条件筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    /** API发布者用户ID，按发布者筛选 */
    private Long userId;

    /** 搜索关键词，匹配API名称或描述 */
    private String keyword;

    /** API分类ID，按分类筛选 */
    private Long typeId;

    /** API状态，可选值：pending/approved/rejected/offline */
    private String status;

    /** 排序字段，如price/createdAt等 */
    private String sortBy;

    /** 排序方向，asc-升序，desc-降序 */
    private String sortOrder;

    /** 作者用户名，按作者名筛选 */
    private String authorName;

}
