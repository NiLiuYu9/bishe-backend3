package com.api.platform.service;

import com.api.platform.vo.ApiVO;

import java.math.BigDecimal;

public interface ApiCacheService {

    ApiVO getApiDetailFromCache(Long id);

    void cacheApiDetail(Long id, ApiVO apiVO);

    void deleteApiDetailCache(Long id);

    void cacheNullValue(Long id);

    boolean isNullValueCached(Long id);

    void clearRateLimitCache(Long apiId);

    Long getApiIdByPath(String endpoint, String method);

    void cachePathMapping(String endpoint, String method, Long apiId);

    void deletePathMapping(String endpoint, String method);

    void updateApiStatistics(Long id, Long invokeCount, Long successCount, Long failCount, BigDecimal rating);

    void clearListCache();
}
