package com.api.platform.service;

import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.vo.ApiStatisticsVO;
import com.api.platform.vo.PlatformStatisticsVO;

public interface ApiInvokeService {

    void recordInvoke(Long apiId, String apiName, Long callerId, Long apiOwnerId, boolean success);

    PlatformStatisticsVO getPlatformStatistics(StatisticsQueryDTO queryDTO);

    ApiStatisticsVO getApiStatistics(Long apiId, StatisticsQueryDTO queryDTO);

    ApiStatisticsVO getUserInvokeStatistics(StatisticsQueryDTO queryDTO);

    ApiStatisticsVO getUserApiInvokeStatistics(StatisticsQueryDTO queryDTO);
}
