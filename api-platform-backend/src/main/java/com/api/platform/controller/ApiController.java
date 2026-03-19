package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.ApiCreateDTO;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiStatusDTO;
import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.ApiTypeVO;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.PageResultVO;
import com.api.platform.service.ApiInfoService;
import com.api.platform.service.ApiTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private ApiTypeService apiTypeService;

    @GetMapping("/list")
    public Result<PageResultVO<ApiVO>> getPublicApiList(ApiQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
        queryDTO.setStatus("approved");
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO, userId);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @GetMapping("/detail/{id}")
    public Result<ApiVO> getApiDetail(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
        ApiVO apiVO = apiInfoService.getApiDetailById(id, userId);
        if (apiVO == null) {
            return Result.failed("API不存在");
        }
        return Result.success(apiVO);
    }

    @GetMapping("/getApis")
    public Result<PageResultVO<ApiVO>> getMyApis(ApiQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        queryDTO.setUserId(userId);
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @PostMapping("/create")
    public Result<ApiVO> createApi(@Validated @RequestBody ApiCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiVO apiVO = apiInfoService.createApi(userId, createDTO);
        return Result.success(apiVO);
    }

    @PutMapping("/update/{id}")
    public Result<ApiVO> updateApi(@PathVariable Long id, @Validated @RequestBody ApiCreateDTO updateDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiVO apiVO = apiInfoService.updateApi(userId, id, updateDTO);
        return Result.success(apiVO);
    }

    @PutMapping("/updateStatus/{id}")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody ApiStatusDTO statusDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiInfoService.updateApiStatus(userId, id, statusDTO);
        return Result.success();
    }

    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO) {
        queryDTO.setStatus("active");
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

}
