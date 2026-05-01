package com.api.platform.service;

import com.api.platform.dto.ApiCreateDTO;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiStatusDTO;
import com.api.platform.dto.AuditApiDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * API信息服务接口 —— 定义API信息的核心业务操作
 *
 * 所属业务模块：API管理模块
 * 包括API的创建、更新、上下架、审核、分类查询等功能
 * 实现类为 ApiInfoServiceImpl
 */
public interface ApiInfoService extends IService<ApiInfo> {

    /**
     * 分页查询API列表（未登录用户）
     *
     * 不包含用户收藏状态信息
     *
     * @param queryDTO 查询条件（关键词、分类、状态、分页参数）
     * @return IPage<ApiVO> 分页API信息列表
     */
    IPage<ApiVO> getApis(ApiQueryDTO queryDTO);

    /**
     * 分页查询API列表（已登录用户）
     *
     * 包含当前用户的收藏状态信息
     *
     * @param queryDTO       查询条件（关键词、分类、状态、分页参数）
     * @param currentUserId  当前登录用户 ID，用于查询收藏状态
     * @return IPage<ApiVO> 分页API信息列表（含收藏状态）
     */
    IPage<ApiVO> getApis(ApiQueryDTO queryDTO, Long currentUserId);

    /**
     * 根据ID获取API详情（未登录用户）
     *
     * @param id API ID
     * @return ApiVO API详细信息
     */
    ApiVO getApiDetailById(Long id);

    /**
     * 根据ID获取API详情（已登录用户）
     *
     * 包含当前用户的收藏状态信息
     *
     * @param id            API ID
     * @param currentUserId 当前登录用户 ID，用于查询收藏状态
     * @return ApiVO API详细信息（含收藏状态）
     */
    ApiVO getApiDetailById(Long id, Long currentUserId);

    /**
     * 创建API
     *
     * @param userId     创建者用户 ID
     * @param createDTO  API创建表单（名称、描述、接口地址、请求方式、分类等）
     * @return ApiVO 创建后的API信息
     */
    ApiVO createApi(Long userId, ApiCreateDTO createDTO);

    /**
     * 更新API信息
     *
     * 仅API创建者可更新
     *
     * @param userId     操作者用户 ID，需为API创建者
     * @param apiId      待更新的API ID
     * @param updateDTO  API更新表单（名称、描述、接口地址等）
     * @return ApiVO 更新后的API信息
     */
    ApiVO updateApi(Long userId, Long apiId, ApiCreateDTO updateDTO);

    /**
     * 更新API状态（上下架）
     *
     * 仅API创建者可操作，上架需通过审核
     *
     * @param userId    操作者用户 ID，需为API创建者
     * @param apiId     API ID
     * @param statusDTO 状态变更表单（目标状态）
     */
    void updateApiStatus(Long userId, Long apiId, ApiStatusDTO statusDTO);

    /**
     * 审核API（管理端）
     *
     * 管理员审核待审核状态的API，通过或驳回
     *
     * @param apiId       待审核的API ID
     * @param auditApiDTO 审核表单（审核结果、审核意见）
     */
    void auditApi(Long apiId, AuditApiDTO auditApiDTO);

}
