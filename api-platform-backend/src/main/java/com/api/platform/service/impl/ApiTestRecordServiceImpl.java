package com.api.platform.service.impl;

import com.api.platform.entity.ApiTestRecord;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiTestRecordMapper;
import com.api.platform.service.ApiTestRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * API测试记录服务实现 —— 处理API在线测试记录的保存、查询、删除
 *
 * 测试记录分两种类型：
 * - 自动记录(type=0)：在线测试API时自动保存的调用记录
 * - 手动保存(type=1)：用户主动保存的测试记录
 */
@Service
public class ApiTestRecordServiceImpl extends ServiceImpl<ApiTestRecordMapper, ApiTestRecord> implements ApiTestRecordService {

    @Override
    public int countByUserIdAndApiId(Long userId, Long apiId) {
        return baseMapper.countByUserIdAndApiId(userId, apiId);
    }

    @Override
    public int countTodayCallsByUserIdAndApiId(Long userId, Long apiId) {
        return baseMapper.countTodayCallsByUserIdAndApiId(userId, apiId);
    }

    @Override
    public void saveRecord(Long userId, Long apiId, String apiName, String params, String result,
                           boolean success, String errorMsg, Integer responseTime, Integer statusCode) {
        ApiTestRecord record = new ApiTestRecord();
        record.setUserId(userId);
        record.setApiId(apiId);
        record.setApiName(apiName);
        record.setParams(params);
        record.setResult(result);
        record.setSuccess(success ? 1 : 0);
        record.setErrorMsg(errorMsg);
        record.setResponseTime(responseTime);
        record.setStatusCode(statusCode);
        record.setType(ApiTestRecord.TYPE_MANUAL_SAVE);
        save(record);
    }

    @Override
    public void saveAutoCallRecord(Long userId, Long apiId, String apiName, String params, String result,
                                   boolean success, String errorMsg, Integer responseTime, Integer statusCode) {
        ApiTestRecord record = new ApiTestRecord();
        record.setUserId(userId);
        record.setApiId(apiId);
        record.setApiName(apiName);
        record.setParams(params);
        record.setResult(result);
        record.setSuccess(success ? 1 : 0);
        record.setErrorMsg(errorMsg);
        record.setResponseTime(responseTime);
        record.setStatusCode(statusCode);
        record.setType(ApiTestRecord.TYPE_AUTO_CALL);
        save(record);
    }

    @Override
    public List<ApiTestRecord> getRecordsByUserIdAndApiId(Long userId, Long apiId) {
        LambdaQueryWrapper<ApiTestRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiTestRecord::getUserId, userId)
                .eq(ApiTestRecord::getApiId, apiId)
                .eq(ApiTestRecord::getType, ApiTestRecord.TYPE_MANUAL_SAVE)
                .orderByDesc(ApiTestRecord::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public void deleteRecord(Long userId, Long recordId) {
        ApiTestRecord record = getById(recordId);
        if (record == null) {
            throw new BusinessException("测试记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该测试记录");
        }
        removeById(recordId);
    }

}
