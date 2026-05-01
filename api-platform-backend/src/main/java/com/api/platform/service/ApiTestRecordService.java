package com.api.platform.service;

import com.api.platform.entity.ApiTestRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 测试记录服务接口 —— 定义API测试记录相关的业务操作
 *
 * 所属业务模块：API管理模块
 * 包括测试记录的保存、查询、删除等功能，支持手动测试和自动调用两种记录类型
 * 实现类为 ApiTestRecordServiceImpl
 */
public interface ApiTestRecordService extends IService<ApiTestRecord> {

    /**
     * 统计用户对指定API的测试记录总数
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return int 测试记录总数
     */
    int countByUserIdAndApiId(Long userId, Long apiId);

    /**
     * 统计用户今日对指定API的测试调用次数
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return int 今日测试调用次数
     */
    int countTodayCallsByUserIdAndApiId(Long userId, Long apiId);

    /**
     * 保存手动测试记录
     *
     * 用户在API测试页面手动发起调用时记录
     *
     * @param userId      用户 ID
     * @param apiId       API ID
     * @param apiName     API 名称
     * @param params      请求参数
     * @param result      响应结果
     * @param success     是否成功
     * @param errorMsg    错误信息（失败时）
     * @param responseTime 响应耗时（毫秒）
     * @param statusCode  HTTP 状态码
     */
    void saveRecord(Long userId, Long apiId, String apiName, String params, String result,
                    boolean success, String errorMsg, Integer responseTime, Integer statusCode);

    /**
     * 保存自动调用测试记录
     *
     * 通过AK/SK自动调用API时记录
     *
     * @param userId      用户 ID
     * @param apiId       API ID
     * @param apiName     API 名称
     * @param params      请求参数
     * @param result      响应结果
     * @param success     是否成功
     * @param errorMsg    错误信息（失败时）
     * @param responseTime 响应耗时（毫秒）
     * @param statusCode  HTTP 状态码
     */
    void saveAutoCallRecord(Long userId, Long apiId, String apiName, String params, String result,
                            boolean success, String errorMsg, Integer responseTime, Integer statusCode);

    /**
     * 查询用户对指定API的测试记录列表
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return List<ApiTestRecord> 测试记录列表
     */
    List<ApiTestRecord> getRecordsByUserIdAndApiId(Long userId, Long apiId);

    /**
     * 删除测试记录
     *
     * 仅记录所有者可删除
     *
     * @param userId   用户 ID
     * @param recordId 记录 ID
     */
    void deleteRecord(Long userId, Long recordId);

}
