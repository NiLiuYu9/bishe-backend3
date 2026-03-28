package com.api.platform.service;

import com.api.platform.dto.PageQueryDTO;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MatchingService {

    double calculateSimilarity(String tag1, String tag2);

    double calculateMatchScore(java.util.List<String> userTags, java.util.List<String> requirementTags);

    IPage<RequirementVO> getRecommendedRequirements(Long userId, PageQueryDTO queryDTO);

}
