package com.api.platform.service;

import java.util.List;
import java.util.Map;

/**
 * 需求标签服务接口 —— 定义需求标签相关的业务操作
 *
 * 所属业务模块：标签管理模块
 * 包括需求标签的保存、查询等功能，标签用于需求分类和智能匹配
 * 实现类为 RequirementTagServiceImpl
 */
public interface RequirementTagService {

    /**
     * 获取需求的所有标签
     *
     * @param requirementId 需求 ID
     * @return List<String> 标签名称列表
     */
    List<String> getTagsByRequirementId(Long requirementId);

    /**
     * 保存需求标签（全量覆盖）
     *
     * 清除需求原有标签，保存新的标签列表
     *
     * @param requirementId 需求 ID
     * @param tags          标签名称列表
     */
    void saveRequirementTags(Long requirementId, List<String> tags);

    /**
     * 批量获取多个需求的标签
     *
     * @param requirementIds 需求 ID 列表
     * @return Map<Long, List<String>> 需求ID与标签列表的映射
     */
    Map<Long, List<String>> getTagsByRequirementIds(List<Long> requirementIds);

}
