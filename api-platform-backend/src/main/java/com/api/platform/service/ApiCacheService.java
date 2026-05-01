package com.api.platform.service;

import com.api.platform.vo.ApiVO;

import java.math.BigDecimal;

/**
 * API缓存服务接口 —— 定义API相关的缓存操作
 *
 * 所属业务模块：缓存管理模块
 * 包括API详情缓存、路径映射缓存、空值缓存、统计信息缓存、列表缓存等功能
 * 实现类为 ApiCacheServiceImpl
 */
public interface ApiCacheService {

    /**
     * 从缓存获取API详情
     *
     * 优先从 Redis 缓存获取，缓存未命中时返回 null（由调用方负责回源）
     *
     * @param id API ID
     * @return ApiVO 缓存中的API详情，未命中返回 null
     */
    ApiVO getApiDetailFromCache(Long id);

    /**
     * 缓存API详情
     *
     * 将API详情写入 Redis 缓存
     *
     * @param id    API ID
     * @param apiVO API详情数据
     */
    void cacheApiDetail(Long id, ApiVO apiVO);

    /**
     * 删除API详情缓存
     *
     * API信息变更时调用，使缓存失效
     *
     * @param id API ID
     */
    void deleteApiDetailCache(Long id);

    /**
     * 缓存空值
     *
     * 防止缓存穿透，当API不存在时缓存空值标记
     *
     * @param id API ID
     */
    void cacheNullValue(Long id);

    /**
     * 判断是否缓存了空值
     *
     * @param id API ID
     * @return boolean 空值已缓存返回 true
     */
    boolean isNullValueCached(Long id);

    /**
     * 清除API限流缓存
     *
     * API限流配置变更时调用
     *
     * @param apiId API ID
     */
    void clearRateLimitCache(Long apiId);

    /**
     * 根据接口路径和请求方式获取API ID
     *
     * 从缓存的路径映射中查询
     *
     * @param endpoint 接口路径（如 /api/weather）
     * @param method   请求方式（GET、POST 等）
     * @return Long API ID，未找到返回 null
     */
    Long getApiIdByPath(String endpoint, String method);

    /**
     * 缓存接口路径映射
     *
     * 将接口路径+请求方式映射到API ID，用于网关路由
     *
     * @param endpoint 接口路径
     * @param method   请求方式
     * @param apiId    API ID
     */
    void cachePathMapping(String endpoint, String method, Long apiId);

    /**
     * 删除接口路径映射缓存
     *
     * API路径变更时调用
     *
     * @param endpoint 接口路径
     * @param method   请求方式
     */
    void deletePathMapping(String endpoint, String method);

    /**
     * 更新API统计信息缓存
     *
     * 更新缓存中的调用次数、成功次数、失败次数、评分
     *
     * @param id           API ID
     * @param invokeCount  总调用次数
     * @param successCount 成功调用次数
     * @param failCount    失败调用次数
     * @param rating       平均评分
     */
    void updateApiStatistics(Long id, Long invokeCount, Long successCount, Long failCount, BigDecimal rating);

    /**
     * 清除API列表缓存
     *
     * API上下架、审核等操作导致列表数据变更时调用
     */
    void clearListCache();

}
