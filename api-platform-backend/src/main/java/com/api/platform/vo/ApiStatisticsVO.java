package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * API统计响应数据
 *
 * 返回API的调用统计数据，包括当前周期和上一周期的对比数据
 */
@Data
public class ApiStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前周期总调用次数 */
    private Long invokeCount;

    /** 当前周期成功调用次数 */
    private Long successCount;

    /** 当前周期失败调用次数 */
    private Long failCount;

    /** 每日统计列表 */
    private List<DailyStatsVO> dailyStats;

    /** 上一周期总调用次数 */
    private Long prevInvokeCount;

    /** 上一周期成功调用次数 */
    private Long prevSuccessCount;

    /** 上一周期失败调用次数 */
    private Long prevFailCount;
}
