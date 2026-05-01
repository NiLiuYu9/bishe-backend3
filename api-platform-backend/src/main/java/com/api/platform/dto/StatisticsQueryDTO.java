package com.api.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 统计查询参数
 *
 * 用于平台统计和API调用统计接口，支持按用户、API名称、时间范围、分类和状态筛选
 */
@Data
public class StatisticsQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID，按指定用户筛选统计 */
    private Long userId;

    /** API名称，按API名称模糊筛选 */
    private String apiName;

    /** 统计开始日期 */
    private LocalDate startDate;

    /** 统计结束日期 */
    private LocalDate endDate;

    /** API分类ID，按分类筛选 */
    private Long typeId;

    /** API状态，按状态筛选 */
    private String status;

    /** 时间范围快捷选项，如today/week/month/year */
    private String timeRange;
}
