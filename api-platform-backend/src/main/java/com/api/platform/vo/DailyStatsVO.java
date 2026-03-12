package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DailyStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String date;

    private Long invokeCount;

    private Long successCount;

    private Long failCount;

    private Long activeUsers;

    private Long pageViews;

    private Long newUsers;

    private Long newOrders;

    private Double successRate;
}
