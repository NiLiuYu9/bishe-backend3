package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long invokeCount;

    private Long successCount;

    private Long failCount;

    private List<DailyStatsVO> dailyStats;

    private Long prevInvokeCount;

    private Long prevSuccessCount;

    private Long prevFailCount;
}
