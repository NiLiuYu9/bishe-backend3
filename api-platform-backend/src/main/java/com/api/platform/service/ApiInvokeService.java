package com.api.platform.service;

import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.vo.ApiStatisticsVO;
import com.api.platform.vo.PlatformStatisticsVO;

/**
 * API调用服务接口 —— 定义API调用统计相关的业务操作
 *
 * 所属业务模块：统计管理模块
 * 包括调用记录、平台统计、API统计、用户调用统计等功能
 * 实现类为 ApiInvokeServiceImpl
 */
public interface ApiInvokeService {

    /**
     * 记录API调用
     *
     * 将调用信息记录到 Redis，用于实时统计和限流
     *
     * @param apiId       API ID
     * @param apiName     API 名称
     * @param callerId    调用者用户 ID
     * @param apiOwnerId  API 所有者用户 ID
     * @param success     调用是否成功
     */
    void recordInvoke(Long apiId, String apiName, Long callerId, Long apiOwnerId, boolean success);

    /**
     * 获取平台整体统计数据
     *
     * 包括总调用量、成功率、日活用户数等
     *
     * @param queryDTO 查询条件（时间范围）
     * @return PlatformStatisticsVO 平台统计数据
     */
    PlatformStatisticsVO getPlatformStatistics(StatisticsQueryDTO queryDTO);

    /**
     * 获取指定API的统计数据
     *
     * 包括API调用量、成功率、平均响应时间等
     *
     * @param apiId    API ID
     * @param queryDTO 查询条件（时间范围）
     * @return ApiStatisticsVO API统计数据
     */
    ApiStatisticsVO getApiStatistics(Long apiId, StatisticsQueryDTO queryDTO);

    /**
     * 获取用户调用API的统计数据
     *
     * 统计用户作为调用方的调用情况
     *
     * @param queryDTO 查询条件（用户 ID、时间范围）
     * @return ApiStatisticsVO 用户调用统计数据
     */
    ApiStatisticsVO getUserInvokeStatistics(StatisticsQueryDTO queryDTO);

    /**
     * 获取用户API被调用的统计数据
     *
     * 统计用户发布的API被其他用户调用的情况
     *
     * @param queryDTO 查询条件（用户 ID、时间范围）
     * @return ApiStatisticsVO 用户API被调用统计数据
     */
    ApiStatisticsVO getUserApiInvokeStatistics(StatisticsQueryDTO queryDTO);

}
