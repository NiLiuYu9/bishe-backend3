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

@RestController
@RequestMapping("/admin")
public class AdminStatisticsController {

    @Autowired
    private ApiInvokeService apiInvokeService;

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
