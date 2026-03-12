package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.dto.ApiCreateDTO;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiStatusDTO;
import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.dto.StatisticsQueryDTO;
import com.api.platform.vo.ApiStatisticsVO;
import com.api.platform.vo.ApiTypeVO;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.PageResultVO;
import com.api.platform.service.ApiInfoService;
import com.api.platform.service.ApiInvokeService;
import com.api.platform.service.ApiTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private ApiTypeService apiTypeService;

    @Autowired
    private ApiInvokeService apiInvokeService;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/list")
    public Result<PageResultVO<ApiVO>> getPublicApiList(ApiQueryDTO queryDTO) {
        queryDTO.setStatus("approved");
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @GetMapping("/detail/{id}")
    public Result<ApiVO> getApiDetail(@PathVariable Long id) {
        ApiVO apiVO = apiInfoService.getApiDetailById(id);
        if (apiVO == null) {
            return Result.failed("API不存在");
        }
        return Result.success(apiVO);
    }

    @GetMapping("/getApis")
    public Result<PageResultVO<ApiVO>> getMyApis(ApiQueryDTO queryDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        queryDTO.setUserId(userId);
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @PostMapping("/create")
    public Result<ApiVO> createApi(@Validated @RequestBody ApiCreateDTO createDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        ApiVO apiVO = apiInfoService.createApi(userId, createDTO);
        return Result.success(apiVO);
    }

    @PutMapping("/update/{id}")
    public Result<ApiVO> updateApi(@PathVariable Long id, @Validated @RequestBody ApiCreateDTO updateDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        ApiVO apiVO = apiInfoService.updateApi(userId, id, updateDTO);
        return Result.success(apiVO);
    }

    @PutMapping("/updateStatus/{id}")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody ApiStatusDTO statusDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        apiInfoService.updateApiStatus(userId, id, statusDTO);
        return Result.success();
    }

    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO) {
        queryDTO.setStatus("active");
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

    @GetMapping("/statistics/{apiId}")
    public Result<ApiStatisticsVO> getApiStatistics(
            @PathVariable Long apiId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        if (startDate != null && !startDate.isEmpty()) {
            queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
        }
        ApiStatisticsVO vo = apiInvokeService.getApiStatistics(apiId, queryDTO);
        return Result.success(vo);
    }

    @GetMapping("/statistics/my-invoke")
    public Result<ApiStatisticsVO> getMyInvokeStatistics(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        queryDTO.setUserId(userId);
        if (startDate != null && !startDate.isEmpty()) {
            queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
        }
        queryDTO.setApiName(apiName);
        queryDTO.setTypeId(typeId);
        queryDTO.setStatus(status);
        queryDTO.setTimeRange(timeRange);
        ApiStatisticsVO vo = apiInvokeService.getUserInvokeStatistics(queryDTO);
        return Result.success(vo);
    }

    @GetMapping("/statistics/my-api-invoke")
    public Result<ApiStatisticsVO> getMyApiInvokeStatistics(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {
        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        queryDTO.setUserId(userId);
        if (startDate != null && !startDate.isEmpty()) {
            queryDTO.setStartDate(LocalDate.parse(startDate, DATE_FORMATTER));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryDTO.setEndDate(LocalDate.parse(endDate, DATE_FORMATTER));
        }
        queryDTO.setApiName(apiName);
        queryDTO.setTypeId(typeId);
        queryDTO.setStatus(status);
        queryDTO.setTimeRange(timeRange);
        ApiStatisticsVO vo = apiInvokeService.getUserApiInvokeStatistics(queryDTO);
        return Result.success(vo);
    }

}
