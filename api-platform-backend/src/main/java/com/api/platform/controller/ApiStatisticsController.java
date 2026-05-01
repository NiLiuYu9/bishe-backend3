package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.exception.BusinessException;
import com.api.platform.service.ApiInvokeService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.ApiStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * API调用统计控制器 —— 处理用户维度的API调用统计数据查询请求
 *
 * 路由前缀：/api/statistics
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/api/statistics")
public class ApiStatisticsController {

    @Autowired
    private ApiInvokeService apiInvokeService;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取指定API的调用统计
     *
     * 返回单个API维度的调用次数、成功率等统计数据
     *
     * @param apiId     API ID
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @return Result&lt;ApiStatisticsVO&gt; API调用统计数据
     */
    @GetMapping("/{apiId}")
    public Result<ApiStatisticsVO> getApiStatistics(
            @PathVariable Long apiId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        if (startDate != null && !startDate.isEmpty()) {
            try {
                queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        if (endDate != null && !endDate.isEmpty()) {
            try {
                queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        ApiStatisticsVO vo = apiInvokeService.getApiStatistics(apiId, queryDTO);
        return Result.success(vo);
    }

    /**
     * 获取当前用户作为调用方的统计
     *
     * 返回当前用户调用所有API的统计数据
     *
     * @param session   HttpSession，用于获取当前登录用户ID
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @param apiName   API名称（模糊搜索）
     * @param typeId    API分类ID
     * @param status    API状态
     * @param timeRange 时间快捷范围
     * @return Result&lt;ApiStatisticsVO&gt; 用户调用统计数据
     */
    @GetMapping("/my-invoke")
    public Result<ApiStatisticsVO> getMyInvokeStatistics(
            HttpSession session,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {
        Long userId = SessionUtils.getCurrentUserId(session);
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        queryDTO.setUserId(userId);
        if (startDate != null && !startDate.isEmpty()) {
            try {
                queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        if (endDate != null && !endDate.isEmpty()) {
            try {
                queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        queryDTO.setApiName(apiName);
        queryDTO.setTypeId(typeId);
        queryDTO.setStatus(status);
        queryDTO.setTimeRange(timeRange);
        ApiStatisticsVO vo = apiInvokeService.getUserInvokeStatistics(queryDTO);
        return Result.success(vo);
    }

    /**
     * 获取当前用户作为API发布者的调用统计
     *
     * 返回当前用户发布的所有API被他人调用的统计数据
     *
     * @param session   HttpSession，用于获取当前登录用户ID
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @param apiName   API名称（模糊搜索）
     * @param typeId    API分类ID
     * @param status    API状态
     * @param timeRange 时间快捷范围
     * @return Result&lt;ApiStatisticsVO&gt; 用户发布的API被调用统计数据
     */
    @GetMapping("/my-api-invoke")
    public Result<ApiStatisticsVO> getMyApiInvokeStatistics(
            HttpSession session,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {
        Long userId = SessionUtils.getCurrentUserId(session);
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        queryDTO.setUserId(userId);
        if (startDate != null && !startDate.isEmpty()) {
            try {
                queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        if (endDate != null && !endDate.isEmpty()) {
            try {
                queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
            } catch (Exception e) {
                throw new BusinessException(400, "日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }
        queryDTO.setApiName(apiName);
        queryDTO.setTypeId(typeId);
        queryDTO.setStatus(status);
        queryDTO.setTimeRange(timeRange);
        ApiStatisticsVO vo = apiInvokeService.getUserApiInvokeStatistics(queryDTO);
        return Result.success(vo);
    }

}
