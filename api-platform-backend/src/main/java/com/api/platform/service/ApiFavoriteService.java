package com.api.platform.service;

import com.api.platform.entity.ApiFavorite;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 收藏服务接口 —— 定义API收藏相关的业务操作
 *
 * 所属业务模块：API管理模块
 * 包括收藏API、取消收藏、判断收藏状态、查询收藏列表等功能
 * 实现类为 ApiFavoriteServiceImpl
 */
public interface ApiFavoriteService extends IService<ApiFavorite> {

    /**
     * 收藏API
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     */
    void addFavorite(Long userId, Long apiId);

    /**
     * 取消收藏API
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     */
    void removeFavorite(Long userId, Long apiId);

    /**
     * 判断用户是否已收藏该API
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return boolean 已收藏返回 true
     */
    boolean isFavorited(Long userId, Long apiId);

    /**
     * 获取用户收藏的所有API ID列表
     *
     * @param userId 用户 ID
     * @return List<Long> API ID 列表
     */
    List<Long> getUserFavoriteApiIds(Long userId);

    /**
     * 分页查询用户收藏的API列表
     *
     * @param userId   用户 ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return IPage<ApiVO> 分页API信息列表
     */
    IPage<ApiVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);

}
