package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiInvokeDaily;
import com.api.platform.entity.OrderInfo;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiInvokeDailyMapper;
import com.api.platform.mapper.OrderInfoMapper;
import com.api.platform.service.ApiInvokeService;
import com.api.platform.vo.ApiCallRankingVO;
import com.api.platform.vo.ApiStatisticsVO;
import com.api.platform.vo.DailyStatsVO;
import com.api.platform.vo.PlatformStatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * API调用统计服务实现 —— 处理API调用次数记录和统计分析
 *
 * 调用统计流程：
 * 1. 网关每次转发API请求后，通过Dubbo调用记录调用次数
 * 2. 调用次数先写入Redis（高性能），再定时同步到MySQL（持久化）
 * 3. 提供按API、按用户、按时间维度的统计查询
 */
@Service
public class ApiInvokeServiceImpl implements ApiInvokeService {

    private static final String INVOKE_KEY_PREFIX = "invoke:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ApiInvokeDailyMapper apiInvokeDailyMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Override
    public void recordInvoke(Long apiId, String apiName, Long callerId, Long apiOwnerId, boolean success) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        String key = INVOKE_KEY_PREFIX + callerId + ":" + apiOwnerId + ":" + apiId + ":" + date;
        
        stringRedisTemplate.opsForHash().increment(key, "total", 1);
        stringRedisTemplate.opsForHash().increment(key, success ? "success" : "fail", 1);
        stringRedisTemplate.opsForHash().put(key, "apiName", apiName);
    }

    @Override
    public PlatformStatisticsVO getPlatformStatistics(StatisticsQueryDTO queryDTO) {
        calculateDateRange(queryDTO);
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(days);
        LocalDate prevEndDate = startDate.minusDays(1);

        List<ApiInvokeDaily> records = queryInvokeRecords(queryDTO, startDate, endDate);
        List<ApiInvokeDaily> prevRecords = queryInvokeRecords(queryDTO, prevStartDate, prevEndDate);

        PlatformStatisticsVO vo = new PlatformStatisticsVO();
        
        long totalInvoke = records.stream().mapToLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0).sum();
        long totalSuccess = records.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum();
        long totalFail = records.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum();

        vo.setTotalApis((long) records.stream().map(ApiInvokeDaily::getApiId).distinct().count());
        vo.setTotalUsers((long) records.stream().map(ApiInvokeDaily::getCallerId).distinct().count());
        vo.setDailyActiveUsers((long) records.stream().map(ApiInvokeDaily::getCallerId).distinct().count());
        vo.setDailyPageViews(totalInvoke);
        
        LambdaQueryWrapper<OrderInfo> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(OrderInfo::getStatus, "paid")
                .or()
                .eq(OrderInfo::getStatus, "completed");
        List<OrderInfo> paidOrders = orderInfoMapper.selectList(orderQueryWrapper);
        vo.setTotalOrders((long) paidOrders.size());
        
        BigDecimal totalRevenue = paidOrders.stream()
                .map(OrderInfo::getPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRevenue(totalRevenue);

        long prevTotalInvoke = prevRecords.stream().mapToLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0).sum();
        vo.setPrevTotalApis((long) prevRecords.stream().map(ApiInvokeDaily::getApiId).distinct().count());
        vo.setPrevTotalUsers((long) prevRecords.stream().map(ApiInvokeDaily::getCallerId).distinct().count());
        vo.setPrevDailyActiveUsers((long) prevRecords.stream().map(ApiInvokeDaily::getCallerId).distinct().count());
        vo.setPrevDailyPageViews(prevTotalInvoke);
        
        LambdaQueryWrapper<OrderInfo> prevOrderQueryWrapper = new LambdaQueryWrapper<>();
        prevOrderQueryWrapper.eq(OrderInfo::getStatus, "paid")
                .or()
                .eq(OrderInfo::getStatus, "completed");
        List<OrderInfo> allPaidOrders = orderInfoMapper.selectList(prevOrderQueryWrapper);
        vo.setPrevTotalOrders((long) allPaidOrders.size());
        vo.setPrevTotalRevenue(totalRevenue);

        Map<Long, Long> apiCountMap = records.stream()
                .collect(Collectors.groupingBy(ApiInvokeDaily::getApiId,
                        Collectors.summingLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0)));
        
        List<ApiCallRankingVO> rankingList = apiCountMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    ApiCallRankingVO ranking = new ApiCallRankingVO();
                    ranking.setApiId(entry.getKey());
                    records.stream()
                            .filter(r -> r.getApiId().equals(entry.getKey()))
                            .findFirst()
                            .ifPresent(r -> ranking.setApiName(r.getApiName()));
                    ranking.setInvokeCount(entry.getValue());
                    return ranking;
                })
                .collect(Collectors.toList());
        vo.setApiCallRanking(rankingList);

        Map<LocalDate, List<ApiInvokeDaily>> dailyMap = records.stream()
                .collect(Collectors.groupingBy(ApiInvokeDaily::getStatDate));
        
        List<DailyStatsVO> dailyStatsList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyStatsVO dailyStats = buildDailyStats(date, dailyMap.getOrDefault(date, new ArrayList<>()), queryDTO.getStatus());
            dailyStatsList.add(dailyStats);
        }
        vo.setDailyStats(dailyStatsList);

        return vo;
    }

    @Override
    public ApiStatisticsVO getApiStatistics(Long apiId, StatisticsQueryDTO queryDTO) {
        calculateDateRange(queryDTO);
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(days);
        LocalDate prevEndDate = startDate.minusDays(1);

        LambdaQueryWrapper<ApiInvokeDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInvokeDaily::getApiId, apiId)
                .between(ApiInvokeDaily::getStatDate, startDate, endDate);
        if (StrUtil.isNotBlank(queryDTO.getStatus()) && !"all".equals(queryDTO.getStatus())) {
            applyStatusFilter(queryWrapper, queryDTO.getStatus());
        }

        List<ApiInvokeDaily> records = apiInvokeDailyMapper.selectList(queryWrapper);
        
        LambdaQueryWrapper<ApiInvokeDaily> prevQueryWrapper = new LambdaQueryWrapper<>();
        prevQueryWrapper.eq(ApiInvokeDaily::getApiId, apiId)
                .between(ApiInvokeDaily::getStatDate, prevStartDate, prevEndDate);
        List<ApiInvokeDaily> prevRecords = apiInvokeDailyMapper.selectList(prevQueryWrapper);

        return buildApiStatisticsVO(records, prevRecords, startDate, endDate, queryDTO.getStatus());
    }

    @Override
    public ApiStatisticsVO getUserInvokeStatistics(StatisticsQueryDTO queryDTO) {
        if (queryDTO.getUserId() == null) {
            return new ApiStatisticsVO();
        }

        calculateDateRange(queryDTO);
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(days);
        LocalDate prevEndDate = startDate.minusDays(1);

        List<ApiInvokeDaily> records = queryUserInvokeRecords(queryDTO, startDate, endDate);
        List<ApiInvokeDaily> prevRecords = queryUserInvokeRecords(queryDTO, prevStartDate, prevEndDate);

        return buildApiStatisticsVO(records, prevRecords, startDate, endDate, queryDTO.getStatus());
    }

    @Override
    public ApiStatisticsVO getUserApiInvokeStatistics(StatisticsQueryDTO queryDTO) {
        if (queryDTO.getUserId() == null) {
            return new ApiStatisticsVO();
        }

        calculateDateRange(queryDTO);
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(days);
        LocalDate prevEndDate = startDate.minusDays(1);

        List<ApiInvokeDaily> records = queryUserApiInvokeRecords(queryDTO, startDate, endDate);
        List<ApiInvokeDaily> prevRecords = queryUserApiInvokeRecords(queryDTO, prevStartDate, prevEndDate);

        return buildApiStatisticsVO(records, prevRecords, startDate, endDate, queryDTO.getStatus());
    }

    private void calculateDateRange(StatisticsQueryDTO queryDTO) {
        if (queryDTO.getTimeRange() != null && !queryDTO.getTimeRange().isEmpty()) {
            LocalDate today = LocalDate.now();
            switch (queryDTO.getTimeRange()) {
                case "today":
                    queryDTO.setStartDate(today);
                    queryDTO.setEndDate(today);
                    break;
                case "yesterday":
                    queryDTO.setStartDate(today.minusDays(1));
                    queryDTO.setEndDate(today.minusDays(1));
                    break;
                case "thisWeek":
                    queryDTO.setStartDate(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                    queryDTO.setEndDate(today);
                    break;
                case "lastWeek":
                    LocalDate lastWeekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    queryDTO.setStartDate(lastWeekStart);
                    queryDTO.setEndDate(lastWeekStart.plusDays(6));
                    break;
                case "thisMonth":
                    queryDTO.setStartDate(today.withDayOfMonth(1));
                    queryDTO.setEndDate(today);
                    break;
                case "lastMonth":
                    LocalDate lastMonthStart = today.minusMonths(1).withDayOfMonth(1);
                    queryDTO.setStartDate(lastMonthStart);
                    queryDTO.setEndDate(lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth()));
                    break;
                case "last7days":
                    queryDTO.setStartDate(today.minusDays(6));
                    queryDTO.setEndDate(today);
                    break;
                case "last30days":
                    queryDTO.setStartDate(today.minusDays(29));
                    queryDTO.setEndDate(today);
                    break;
                case "last90days":
                    queryDTO.setStartDate(today.minusDays(89));
                    queryDTO.setEndDate(today);
                    break;
            }
        }
        
        if (queryDTO.getStartDate() == null) {
            queryDTO.setStartDate(LocalDate.now().minusDays(30));
        }
        if (queryDTO.getEndDate() == null) {
            queryDTO.setEndDate(LocalDate.now());
        }
    }

    private List<ApiInvokeDaily> queryInvokeRecords(StatisticsQueryDTO queryDTO, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ApiInvokeDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(ApiInvokeDaily::getStatDate, startDate, endDate);
        
        if (StrUtil.isNotBlank(queryDTO.getApiName())) {
            queryWrapper.like(ApiInvokeDaily::getApiName, queryDTO.getApiName());
        }
        
        if (queryDTO.getTypeId() != null) {
            Set<Long> apiIds = getApiIdsByTypeId(queryDTO.getTypeId());
            if (apiIds.isEmpty()) {
                return new ArrayList<>();
            }
            queryWrapper.in(ApiInvokeDaily::getApiId, apiIds);
        }
        
        if (StrUtil.isNotBlank(queryDTO.getStatus()) && !"all".equals(queryDTO.getStatus())) {
            applyStatusFilter(queryWrapper, queryDTO.getStatus());
        }
        
        return apiInvokeDailyMapper.selectList(queryWrapper);
    }

    private List<ApiInvokeDaily> queryUserInvokeRecords(StatisticsQueryDTO queryDTO, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ApiInvokeDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInvokeDaily::getCallerId, queryDTO.getUserId())
                .between(ApiInvokeDaily::getStatDate, startDate, endDate);
        
        if (StrUtil.isNotBlank(queryDTO.getApiName())) {
            queryWrapper.like(ApiInvokeDaily::getApiName, queryDTO.getApiName());
        }
        
        if (queryDTO.getTypeId() != null) {
            Set<Long> apiIds = getApiIdsByTypeId(queryDTO.getTypeId());
            if (apiIds.isEmpty()) {
                return new ArrayList<>();
            }
            queryWrapper.in(ApiInvokeDaily::getApiId, apiIds);
        }
        
        if (StrUtil.isNotBlank(queryDTO.getStatus()) && !"all".equals(queryDTO.getStatus())) {
            applyStatusFilter(queryWrapper, queryDTO.getStatus());
        }
        
        return apiInvokeDailyMapper.selectList(queryWrapper);
    }

    private List<ApiInvokeDaily> queryUserApiInvokeRecords(StatisticsQueryDTO queryDTO, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ApiInvokeDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInvokeDaily::getApiOwnerId, queryDTO.getUserId())
                .between(ApiInvokeDaily::getStatDate, startDate, endDate);
        
        if (StrUtil.isNotBlank(queryDTO.getApiName())) {
            queryWrapper.like(ApiInvokeDaily::getApiName, queryDTO.getApiName());
        }
        
        if (queryDTO.getTypeId() != null) {
            Set<Long> apiIds = getApiIdsByTypeId(queryDTO.getTypeId());
            if (apiIds.isEmpty()) {
                return new ArrayList<>();
            }
            queryWrapper.in(ApiInvokeDaily::getApiId, apiIds);
        }
        
        if (StrUtil.isNotBlank(queryDTO.getStatus()) && !"all".equals(queryDTO.getStatus())) {
            applyStatusFilter(queryWrapper, queryDTO.getStatus());
        }
        
        return apiInvokeDailyMapper.selectList(queryWrapper);
    }

    private Set<Long> getApiIdsByTypeId(Long typeId) {
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiInfo::getTypeId, typeId);
        List<ApiInfo> apiInfos = apiInfoMapper.selectList(wrapper);
        return apiInfos.stream().map(ApiInfo::getId).collect(Collectors.toSet());
    }

    private void applyStatusFilter(LambdaQueryWrapper<ApiInvokeDaily> queryWrapper, String status) {
        if ("success".equals(status)) {
            queryWrapper.gt(ApiInvokeDaily::getSuccessCount, 0);
        } else if ("fail".equals(status)) {
            queryWrapper.gt(ApiInvokeDaily::getFailCount, 0);
        }
    }

    private DailyStatsVO buildDailyStats(LocalDate date, List<ApiInvokeDaily> dayRecords, String status) {
        DailyStatsVO dailyStats = new DailyStatsVO();
        dailyStats.setDate(date.format(DATE_FORMATTER));
        
        boolean filterSuccess = "success".equals(status);
        boolean filterFail = "fail".equals(status);
        
        long invokeCount;
        long successCount;
        long failCount;
        
        if (filterSuccess) {
            successCount = dayRecords.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum();
            invokeCount = successCount;
            failCount = 0;
        } else if (filterFail) {
            failCount = dayRecords.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum();
            invokeCount = failCount;
            successCount = 0;
        } else {
            invokeCount = dayRecords.stream().mapToLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0).sum();
            successCount = dayRecords.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum();
            failCount = dayRecords.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum();
        }
        
        dailyStats.setInvokeCount(invokeCount);
        dailyStats.setSuccessCount(successCount);
        dailyStats.setFailCount(failCount);
        dailyStats.setActiveUsers((long) dayRecords.stream().map(ApiInvokeDaily::getCallerId).distinct().count());
        dailyStats.setPageViews(invokeCount);
        dailyStats.setNewUsers(0L);
        dailyStats.setNewOrders(0L);
        
        if (invokeCount > 0) {
            double rate = (double) successCount / invokeCount * 100;
            dailyStats.setSuccessRate(BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP).doubleValue());
        } else {
            dailyStats.setSuccessRate(0.0);
        }
        
        return dailyStats;
    }

    private ApiStatisticsVO buildApiStatisticsVO(List<ApiInvokeDaily> records, List<ApiInvokeDaily> prevRecords, 
                                                  LocalDate startDate, LocalDate endDate, String status) {
        ApiStatisticsVO vo = new ApiStatisticsVO();
        
        boolean filterSuccess = "success".equals(status);
        boolean filterFail = "fail".equals(status);
        
        if (filterSuccess) {
            vo.setInvokeCount(records.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setSuccessCount(records.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setFailCount(0L);
            
            vo.setPrevInvokeCount(prevRecords.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setPrevSuccessCount(prevRecords.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setPrevFailCount(0L);
        } else if (filterFail) {
            vo.setInvokeCount(records.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());
            vo.setSuccessCount(0L);
            vo.setFailCount(records.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());
            
            vo.setPrevInvokeCount(prevRecords.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());
            vo.setPrevSuccessCount(0L);
            vo.setPrevFailCount(prevRecords.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());
        } else {
            vo.setInvokeCount(records.stream().mapToLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0).sum());
            vo.setSuccessCount(records.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setFailCount(records.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());

            vo.setPrevInvokeCount(prevRecords.stream().mapToLong(r -> r.getTotalCount() != null ? r.getTotalCount() : 0).sum());
            vo.setPrevSuccessCount(prevRecords.stream().mapToLong(r -> r.getSuccessCount() != null ? r.getSuccessCount() : 0).sum());
            vo.setPrevFailCount(prevRecords.stream().mapToLong(r -> r.getFailCount() != null ? r.getFailCount() : 0).sum());
        }

        Map<LocalDate, List<ApiInvokeDaily>> dailyMap = records.stream()
                .collect(Collectors.groupingBy(ApiInvokeDaily::getStatDate));
        
        List<DailyStatsVO> dailyStatsList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyStatsVO dailyStats = buildDailyStats(date, dailyMap.getOrDefault(date, new ArrayList<>()), status);
            dailyStatsList.add(dailyStats);
        }
        vo.setDailyStats(dailyStatsList);

        return vo;
    }
}
