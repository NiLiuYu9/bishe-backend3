package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 平台统计响应数据
 *
 * 返回平台整体运营统计数据，包括API、用户、订单总量及环比数据
 */
@Data
public class PlatformStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API总数 */
    private Long totalApis;

    /** 用户总数 */
    private Long totalUsers;

    /** 订单总数 */
    private Long totalOrders;

    /** 总收入金额 */
    private BigDecimal totalRevenue;

    /** 日活跃用户数 */
    private Long dailyActiveUsers;

    /** 日页面访问量 */
    private Long dailyPageViews;

    /** API调用排行列表 */
    private List<ApiCallRankingVO> apiCallRanking;

    /** 每日统计列表 */
    private List<DailyStatsVO> dailyStats;

    /** 上一周期API总数 */
    private Long prevTotalApis;

    /** 上一周期用户总数 */
    private Long prevTotalUsers;

    /** 上一周期订单总数 */
    private Long prevTotalOrders;

    /** 上一周期总收入金额 */
    private BigDecimal prevTotalRevenue;

    /** 上一周期日活跃用户数 */
    private Long prevDailyActiveUsers;

    /** 上一周期日页面访问量 */
    private Long prevDailyPageViews;

}
