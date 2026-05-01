package com.api.platform.service;

import com.api.platform.dto.PageQueryDTO;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 智能匹配服务接口 —— 定义基于标签的需求智能推荐操作
 *
 * 所属业务模块：智能匹配模块
 * 包括标签相似度计算、匹配评分计算、个性化需求推荐等功能
 * 基于用户标签与需求标签的相似度，为开发者推荐匹配度最高的需求
 * 实现类为 MatchingServiceImpl
 */
public interface MatchingService {

    /**
     * 计算两个标签之间的相似度
     *
     * 基于标签语义计算相似度分数
     *
     * @param tag1 标签1
     * @param tag2 标签2
     * @return double 相似度分数，范围 [0, 1]
     */
    double calculateSimilarity(String tag1, String tag2);

    /**
     * 计算用户标签与需求标签的匹配评分
     *
     * 综合计算两组标签之间的整体匹配度
     *
     * @param userTags        用户标签列表
     * @param requirementTags 需求标签列表
     * @return double 匹配评分，范围 [0, 1]
     */
    double calculateMatchScore(java.util.List<String> userTags, java.util.List<String> requirementTags);

    /**
     * 获取推荐需求列表
     *
     * 根据用户标签智能匹配需求，按匹配度降序返回
     *
     * @param userId   用户 ID
     * @param queryDTO 分页查询参数
     * @return IPage<RequirementVO> 分页推荐需求列表（含匹配度评分）
     */
    IPage<RequirementVO> getRecommendedRequirements(Long userId, PageQueryDTO queryDTO);

}
