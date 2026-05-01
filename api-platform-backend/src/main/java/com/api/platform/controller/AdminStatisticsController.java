package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.service.ApiInvokeService;
import com.api.platform.vo.PlatformStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 平台统计控制器 —— 处理管理员平台级统计数据查询请求
 *
 * 路由前缀：/admin
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 与 ApiStatisticsController 的区别：
 * - AdminStatisticsController 面向管理员，提供全平台维度的统计数据
 * - ApiStatisticsController 面向普通用户，提供个人维度的调用统计
 */
@RestController
@RequestMapping("/admin")
public class AdminStatisticsController {

    @Autowired
    private ApiInvokeService apiInvokeService;

    /**
     * 获取平台统计数据
     *
     * 支持按日期范围、API名称、分类、状态、时间快捷范围筛选，
     * 返回平台维度的汇总统计信息
     *
     * @param startDate  开始日期，格式 yyyy-MM-dd
     * @param endDate    结束日期，格式 yyyy-MM-dd
     * @param apiName    API名称（模糊搜索）
     * @param typeId     API分类ID
     * @param status     API状态
     * @param timeRange  时间快捷范围（如 7d、30d、90d）
     * @return Result&lt;PlatformStatisticsVO&gt; 平台统计数据
     */
    @GetMapping("/statistics")
    public Result<PlatformStatisticsVO> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDate != null && !startDate.isEmpty()) {
            queryDTO.setStartDate(LocalDate.parse(startDate, formatter));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryDTO.setEndDate(LocalDate.parse(endDate, formatter));
        }
        queryDTO.setApiName(apiName);
        queryDTO.setTypeId(typeId);
        queryDTO.setStatus(status);
        queryDTO.setTimeRange(timeRange);
        PlatformStatisticsVO vo = apiInvokeService.getPlatformStatistics(queryDTO);
        return Result.success(vo);
    }
}
