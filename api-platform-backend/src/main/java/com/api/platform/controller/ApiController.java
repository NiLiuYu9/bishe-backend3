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

/**
 * API管理控制器 —— 处理API的增删改查、上下架状态管理及分类查询请求
 *
 * 路由前缀：/api
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private ApiTypeService apiTypeService;

    /**
     * 获取已审核通过的API公开列表（市场首页）
     *
     * 仅返回 status=approved 的API，支持按分类、关键词筛选和分页。
     * 若用户已登录，会额外返回该用户是否已收藏每个API的标记
     *
     * @param queryDTO 查询条件（关键词、分类ID、分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID（可为空）
     * @return Result&lt;PageResultVO&lt;ApiVO&gt;&gt; 分页的API列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<ApiVO>> getPublicApiList(ApiQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
        queryDTO.setStatus("approved");
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO, userId);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    /**
     * 获取API详情
     *
     * 根据API ID查询详情，若用户已登录会返回收藏状态
     *
     * @param id      API ID
     * @param session HttpSession，用于获取当前登录用户ID（可为空）
     * @return Result&lt;ApiVO&gt; API详情信息
     */
    @GetMapping("/detail/{id}")
    public Result<ApiVO> getApiDetail(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
        ApiVO apiVO = apiInfoService.getApiDetailById(id, userId);
        if (apiVO == null) {
            return Result.failed("API不存在");
        }
        return Result.success(apiVO);
    }

    /**
     * 获取当前用户创建的API列表（我的API）
     *
     * 返回当前用户所有状态的API（包括待审核、已拒绝、已下架等）
     *
     * @param queryDTO 查询条件（状态、关键词、分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;ApiVO&gt;&gt; 分页的API列表
     */
    @GetMapping("/getApis")
    public Result<PageResultVO<ApiVO>> getMyApis(ApiQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        queryDTO.setUserId(userId);
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    /**
     * 创建新API
     *
     * 创建后API状态默认为 pending，需管理员审核通过后才可在市场展示。
     * 限流：10次/分钟
     *
     * @param createDTO API创建表单（名称、描述、接口地址、请求方法、价格等）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;ApiVO&gt; 创建成功的API信息
     */
    @PostMapping("/create")
    public Result<ApiVO> createApi(@Validated @RequestBody ApiCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiVO apiVO = apiInfoService.createApi(userId, createDTO);
        return Result.success(apiVO);
    }

    /**
     * 更新API信息
     *
     * 仅API创建者可更新，更新后状态不变。
     * 限流：20次/分钟
     *
     * @param id        API ID
     * @param updateDTO API更新表单（与创建表单字段相同）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;ApiVO&gt; 更新后的API信息
     */
    @PutMapping("/update/{id}")
    public Result<ApiVO> updateApi(@PathVariable Long id, @Validated @RequestBody ApiCreateDTO updateDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiVO apiVO = apiInfoService.updateApi(userId, id, updateDTO);
        return Result.success(apiVO);
    }

    /**
     * 更新API状态（上架/下架）
     *
     * API创建者可将已审核通过的API上架或下架。
     * 限流：10次/分钟
     *
     * @param id        API ID
     * @param statusDTO 状态变更表单（status: online/offline）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 操作成功无返回数据
     */
    @PutMapping("/updateStatus/{id}")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody ApiStatusDTO statusDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiInfoService.updateApiStatus(userId, id, statusDTO);
        return Result.success();
    }

    /**
     * 获取API分类列表
     *
     * 仅返回 status=active 的分类，用于API创建和市场筛选
     *
     * @param queryDTO 查询条件（分页参数）
     * @return Result&lt;PageResultVO&lt;ApiTypeVO&gt;&gt; 分页的API分类列表
     */
    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO) {
        queryDTO.setStatus("active");
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

}
