package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlatformStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalApis;

    private Long totalUsers;

    private Long totalOrders;

    private BigDecimal totalRevenue;

    private Long dailyActiveUsers;

    private Long dailyPageViews;

    private List<ApiCallRankingVO> apiCallRanking;

    private List<DailyStatsVO> dailyStats;

    private Long prevTotalApis;

    private Long prevTotalUsers;

    private Long prevTotalOrders;

    private BigDecimal prevTotalRevenue;

    private Long prevDailyActiveUsers;

    private Long prevDailyPageViews;
}
