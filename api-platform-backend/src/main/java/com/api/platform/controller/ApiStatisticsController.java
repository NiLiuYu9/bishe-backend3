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

@RestController
@RequestMapping("/api/statistics")
public class ApiStatisticsController {

    @Autowired
    private ApiInvokeService apiInvokeService;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
