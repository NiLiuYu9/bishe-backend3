package com.api.platform.service;

import com.api.platform.entity.ApiTestRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ApiTestRecordService extends IService<ApiTestRecord> {

    int countByUserIdAndApiId(Long userId, Long apiId);

    int countTodayCallsByUserIdAndApiId(Long userId, Long apiId);

    void saveRecord(Long userId, Long apiId, String apiName, String params, String result, 
                    boolean success, String errorMsg, Integer responseTime, Integer statusCode);

    void saveAutoCallRecord(Long userId, Long apiId, String apiName, String params, String result,
                            boolean success, String errorMsg, Integer responseTime, Integer statusCode);

    List<ApiTestRecord> getRecordsByUserIdAndApiId(Long userId, Long apiId);

    void deleteRecord(Long userId, Long recordId);

}
