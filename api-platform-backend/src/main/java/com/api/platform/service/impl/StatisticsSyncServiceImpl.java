package com.api.platform.service.impl;

import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiInvokeDaily;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiInvokeDailyMapper;
import com.api.platform.mapper.OrderInfoMapper;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.StatisticsSyncService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticsSyncServiceImpl implements StatisticsSyncService {

    private static final String INVOKE_KEY_PREFIX = "invoke:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ApiInvokeDailyMapper apiInvokeDailyMapper;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ApiCacheService apiCacheService;

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    public void syncRedisToDatabase() {
        LocalDate today = LocalDate.now();

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(INVOKE_KEY_PREFIX + "*")
                .count(1000)
                .build();

        try (Cursor<String> cursor = stringRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                try {
                    processKey(key, today);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processKey(String key, LocalDate today) {
        String[] parts = key.replace(INVOKE_KEY_PREFIX, "").split(":");
        if (parts.length != 4) {
            return;
        }

        Long callerId = Long.parseLong(parts[0]);
        Long apiOwnerId = Long.parseLong(parts[1]);
        Long apiId = Long.parseLong(parts[2]);
        LocalDate statDate = LocalDate.parse(parts[3], DATE_FORMATTER);

        if (statDate.equals(today)) {
            return;
        }

        Object totalObj = stringRedisTemplate.opsForHash().get(key, "total");
        Object successObj = stringRedisTemplate.opsForHash().get(key, "success");
        Object failObj = stringRedisTemplate.opsForHash().get(key, "fail");
        Object apiNameObj = stringRedisTemplate.opsForHash().get(key, "apiName");

        long total = totalObj != null ? Long.parseLong(totalObj.toString()) : 0;
        long success = successObj != null ? Long.parseLong(successObj.toString()) : 0;
        long fail = failObj != null ? Long.parseLong(failObj.toString()) : 0;
        String apiName = apiNameObj != null ? apiNameObj.toString() : "";

        ApiInvokeDaily entity = new ApiInvokeDaily();
        entity.setApiId(apiId);
        entity.setApiName(apiName);
        entity.setCallerId(callerId);
        entity.setApiOwnerId(apiOwnerId);
        entity.setStatDate(statDate);
        entity.setTotalCount(total);
        entity.setSuccessCount(success);
        entity.setFailCount(fail);

        saveOrUpdate(entity);

        stringRedisTemplate.delete(key);
    }

    private void saveOrUpdate(ApiInvokeDaily entity) {
        ApiInvokeDaily existing = apiInvokeDailyMapper.selectOne(
                new LambdaQueryWrapper<ApiInvokeDaily>()
                        .eq(ApiInvokeDaily::getApiId, entity.getApiId())
                        .eq(ApiInvokeDaily::getCallerId, entity.getCallerId())
                        .eq(ApiInvokeDaily::getStatDate, entity.getStatDate())
        );

        if (existing != null) {
            existing.setTotalCount(existing.getTotalCount() + entity.getTotalCount());
            existing.setSuccessCount(existing.getSuccessCount() + entity.getSuccessCount());
            existing.setFailCount(existing.getFailCount() + entity.getFailCount());
            apiInvokeDailyMapper.updateById(existing);
        } else {
            apiInvokeDailyMapper.insert(entity);
        }
    }

    @Override
    @Scheduled(cron = "0 5 * * * ?")
    public void syncDailyStatisticsToApiInfo() {
        List<ApiInfo> apiInfos = apiInfoMapper.selectList(null);
        for (ApiInfo apiInfo : apiInfos) {
            updateApiInfoStatistics(apiInfo.getId());
        }
    }

    private void updateApiInfoStatistics(Long apiId) {
        LambdaQueryWrapper<ApiInvokeDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInvokeDaily::getApiId, apiId);
        List<ApiInvokeDaily> dailyList = apiInvokeDailyMapper.selectList(queryWrapper);
        
        long totalInvoke = 0;
        long totalSuccess = 0;
        long totalFail = 0;
        
        for (ApiInvokeDaily daily : dailyList) {
            totalInvoke += daily.getTotalCount();
            totalSuccess += daily.getSuccessCount();
            totalFail += daily.getFailCount();
        }
        
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo != null) {
            apiInfo.setInvokeCount(totalInvoke);
            apiInfo.setSuccessCount(totalSuccess);
            apiInfo.setFailCount(totalFail);
            apiInfoMapper.updateById(apiInfo);
            
            apiCacheService.updateApiStatistics(apiId, totalInvoke, totalSuccess, totalFail, apiInfo.getRating());
        }
    }

    @Override
    @Scheduled(cron = "0 10 * * * ?")
    public void syncApiRating() {
        List<ApiInfo> apiInfos = apiInfoMapper.selectList(null);
        for (ApiInfo apiInfo : apiInfos) {
            BigDecimal avgRating = orderInfoMapper.getAverageRatingByApiId(apiInfo.getId());
            if (avgRating != null && avgRating.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rating = avgRating.setScale(1, RoundingMode.HALF_UP);
                apiInfo.setRating(rating);
                apiInfoMapper.updateById(apiInfo);
                
                apiCacheService.updateApiStatistics(apiInfo.getId(), 
                        apiInfo.getInvokeCount(), 
                        apiInfo.getSuccessCount(), 
                        apiInfo.getFailCount(), 
                        rating);
            }
        }
    }
}
