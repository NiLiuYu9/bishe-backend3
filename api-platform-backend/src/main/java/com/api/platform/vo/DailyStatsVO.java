package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日统计响应数据
 *
 * 返回单日的统计数据，包括调用量、活跃用户、页面访问和订单量
 */
@Data
public class DailyStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计日期 */
    private String date;

    /** 当日总调用次数 */
    private Long invokeCount;

    /** 当日成功调用次数 */
    private Long successCount;

    /** 当日失败调用次数 */
    private Long failCount;

    /** 当日活跃用户数 */
    private Long activeUsers;

    /** 当日页面访问量 */
    private Long pageViews;

    /** 当日新增用户数 */
    private Long newUsers;

    /** 当日新增订单数 */
    private Long newOrders;

    /** 当日调用成功率 */
    private Double successRate;

}
