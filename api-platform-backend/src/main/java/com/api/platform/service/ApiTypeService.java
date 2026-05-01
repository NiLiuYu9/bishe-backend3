package com.api.platform.service;

import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.entity.ApiType;
import com.api.platform.vo.ApiTypeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * API分类服务接口 —— 定义API分类相关的业务操作
 *
 * 所属业务模块：API管理模块
 * 包括分类的增删改查、状态启停等功能
 * 实现类为 ApiTypeServiceImpl
 */
public interface ApiTypeService extends IService<ApiType> {

    /**
     * 获取所有API分类
     *
     * @return List<ApiType> 分类列表
     */
    List<ApiType> getAllTypes();

    /**
     * 分页查询API分类
     *
     * @param queryDTO 查询条件（分类名称、状态、分页参数）
     * @return IPage<ApiTypeVO> 分页分类列表
     */
    IPage<ApiTypeVO> pageApiTypes(ApiTypeQueryDTO queryDTO);

    /**
     * 创建API分类
     *
     * @param apiType 分类实体（分类名称、描述、图标等）
     */
    void createType(ApiType apiType);

    /**
     * 更新API分类
     *
     * @param apiType 分类实体（含 ID 和更新字段）
     */
    void updateType(ApiType apiType);

    /**
     * 更新分类状态（启用/禁用）
     *
     * @param id     分类 ID
     * @param status 目标状态（0-禁用，1-启用）
     */
    void updateStatus(Long id, String status);

}
