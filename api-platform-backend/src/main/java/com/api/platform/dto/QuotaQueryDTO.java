package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 配额查询参数
 *
 * 用于用户API调用配额查询接口，支持按用户和API名称筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID，按指定用户筛选配额 */
    private Long userId;

    /** API名称，按API名称模糊筛选 */
    private String apiName;

}
