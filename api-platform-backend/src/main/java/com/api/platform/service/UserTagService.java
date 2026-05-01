package com.api.platform.service;

import java.util.List;

/**
 * 用户标签服务接口 —— 定义用户标签相关的业务操作
 *
 * 所属业务模块：标签管理模块
 * 包括用户标签的增删、查询等功能，标签用于智能匹配推荐
 * 实现类为 UserTagServiceImpl
 */
public interface UserTagService {

    /**
     * 获取用户的所有标签
     *
     * @param userId 用户 ID
     * @return List<String> 标签名称列表
     */
    List<String> getTagsByUserId(Long userId);

    /**
     * 保存用户标签（全量覆盖）
     *
     * 清除用户原有标签，保存新的标签列表
     *
     * @param userId 用户 ID
     * @param tags   标签名称列表
     */
    void saveUserTags(Long userId, List<String> tags);

    /**
     * 为用户添加单个标签
     *
     * 若标签已存在则不重复添加
     *
     * @param userId  用户 ID
     * @param tagName 标签名称
     */
    void addUserTag(Long userId, String tagName);

    /**
     * 移除用户单个标签
     *
     * @param userId  用户 ID
     * @param tagName 标签名称
     */
    void removeUserTag(Long userId, String tagName);

}
