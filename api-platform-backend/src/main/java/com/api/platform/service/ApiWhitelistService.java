package com.api.platform.service;

import com.api.platform.entity.ApiWhitelist;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 白名单服务接口 —— 定义API白名单相关的业务操作
 *
 * 所属业务模块：API管理模块
 * 包括白名单用户的增删、白名单启停、白名单查询等功能
 * 开启白名单后，仅白名单内的用户可调用该API
 * 实现类为 ApiWhitelistServiceImpl
 */
public interface ApiWhitelistService extends IService<ApiWhitelist> {

    /**
     * 批量添加白名单用户
     *
     * 添加用户到API白名单，若白名单未开启则自动开启
     *
     * @param apiId       API ID
     * @param operatorId  操作者用户 ID，需为API创建者
     * @param usernames   待添加的用户名列表
     */
    void addWhitelistUsers(Long apiId, Long operatorId, List<String> usernames);

    /**
     * 移除白名单用户
     *
     * 从API白名单中移除指定用户，若白名单为空则自动关闭白名单
     *
     * @param apiId      API ID
     * @param userId     待移除的用户 ID
     * @param operatorId 操作者用户 ID，需为API创建者
     */
    void removeWhitelistUser(Long apiId, Long userId, Long operatorId);

    /**
     * 分页查询API白名单
     *
     * @param apiId    API ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return IPage<ApiWhitelist> 分页白名单列表
     */
    IPage<ApiWhitelist> getWhitelistPage(Long apiId, int pageNum, int pageSize);

    /**
     * 判断用户是否在API白名单中
     *
     * @param apiId  API ID
     * @param userId 用户 ID
     * @return boolean 在白名单中返回 true
     */
    boolean isInWhitelist(Long apiId, Long userId);

    /**
     * 启用白名单
     *
     * 启用前需确保白名单中至少有一个用户
     *
     * @param apiId      API ID
     * @param operatorId 操作者用户 ID，需为API创建者
     */
    void enableWhitelist(Long apiId, Long operatorId);

    /**
     * 禁用白名单
     *
     * 禁用后所有用户均可调用该API
     *
     * @param apiId      API ID
     * @param operatorId 操作者用户 ID，需为API创建者
     */
    void disableWhitelist(Long apiId, Long operatorId);

}
