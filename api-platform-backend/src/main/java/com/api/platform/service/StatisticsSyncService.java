package com.api.platform.service;

/**
 * 统计同步服务接口 —— 定义Redis到MySQL的统计数据定时同步操作
 *
 * 所属业务模块：统计管理模块
 * 包括日调用统计同步、API信息统计同步、评分同步等功能
 * 由定时任务触发，将 Redis 中的实时统计数据持久化到 MySQL
 * 实现类为 StatisticsSyncServiceImpl
 */
public interface StatisticsSyncService {

    /**
     * 将Redis中的调用统计数据同步到MySQL
     *
     * 将 Redis 中累积的日调用统计（调用量、成功次数、失败次数）写入 api_invoke_daily 表
     */
    void syncRedisToDatabase();

    /**
     * 将日统计汇总数据同步到API信息表
     *
     * 汇总 api_invoke_daily 表中的统计数据，更新 api_info 表的总调用量、成功次数、失败次数
     */
    void syncDailyStatisticsToApiInfo();

    /**
     * 同步API评分
     *
     * 重新计算各API的平均评分，更新 api_info 表的 rating 字段
     */
    void syncApiRating();

}
