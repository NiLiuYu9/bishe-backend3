package com.api.platform.service.impl;

import com.api.platform.dto.PageQueryDTO;
import com.api.platform.entity.Requirement;
import com.api.platform.mapper.RequirementTagMapper;
import com.api.platform.mapper.UserTagMapper;
import com.api.platform.service.MatchingService;
import com.api.platform.service.RequirementTagService;
import com.api.platform.service.UserTagService;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能匹配服务实现 —— 基于用户标签与需求标签的相似度进行需求推荐
 *
 * 匹配算法：Levenshtein编辑距离
 * - 计算用户每个标签与需求每个标签的编辑距离
 * - 编辑距离越小，两个标签越相似
 * - 综合所有标签的相似度得出匹配评分
 * - 按匹配评分降序排列，推荐最匹配的需求
 */
@Service
public class MatchingServiceImpl implements MatchingService {

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private RequirementTagService requirementTagService;

    @Autowired
    private UserTagMapper userTagMapper;

    @Autowired
    private RequirementTagMapper requirementTagMapper;

    @Autowired
    private com.api.platform.mapper.RequirementMapper requirementMapper;

    @Override
    public double calculateSimilarity(String tag1, String tag2) {
        if (tag1 == null || tag2 == null) {
            return 0.0;
        }
        String s1 = tag1.toLowerCase().trim();
        String s2 = tag2.toLowerCase().trim();
        if (s1.equals(s2)) {
            return 1.0;
        }
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) {
            return 1.0;
        }
        return 1.0 - (double) distance / maxLen;
    }

    private int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return dp[m][n];
    }

    @Override
    public double calculateMatchScore(List<String> userTags, List<String> requirementTags) {
        if (userTags == null || userTags.isEmpty() || requirementTags == null || requirementTags.isEmpty()) {
            return 0.0;
        }
        double totalScore = 0.0;
        for (String userTag : userTags) {
            double maxSimilarity = 0.0;
            for (String reqTag : requirementTags) {
                double similarity = calculateSimilarity(userTag, reqTag);
                maxSimilarity = Math.max(maxSimilarity, similarity);
            }
            totalScore += maxSimilarity;
        }
        return totalScore / userTags.size();
    }

    @Override
    public IPage<RequirementVO> getRecommendedRequirements(Long userId, PageQueryDTO queryDTO) {
        List<String> userTags = userTagService.getTagsByUserId(userId);
        if (userTags == null || userTags.isEmpty()) {
            IPage<RequirementVO> emptyPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        LambdaQueryWrapper<Requirement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Requirement::getStatus, "open");
        queryWrapper.ne(Requirement::getUserId, userId);
        queryWrapper.orderByDesc(Requirement::getCreateTime);
        List<Requirement> allRequirements = requirementMapper.selectList(queryWrapper);
        if (allRequirements.isEmpty()) {
            IPage<RequirementVO> emptyPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        List<Long> requirementIds = allRequirements.stream()
                .map(Requirement::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> tagMap = requirementTagService.getTagsByRequirementIds(requirementIds);
        List<RequirementVO> voList = new ArrayList<>();
        for (Requirement requirement : allRequirements) {
            List<String> reqTags = tagMap.getOrDefault(requirement.getId(), Collections.emptyList());
            double score = calculateMatchScore(userTags, reqTags);
            if (score > 0) {
                RequirementVO vo = new RequirementVO();
                org.springframework.beans.BeanUtils.copyProperties(requirement, vo);
                vo.setTags(reqTags);
                vo.setMatchScore(score * 100);
                voList.add(vo);
            }
        }
        voList.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        int total = voList.size();
        int start = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        int end = Math.min(start + queryDTO.getPageSize(), total);
        List<RequirementVO> pagedList;
        if (start >= total) {
            pagedList = Collections.emptyList();
        } else {
            pagedList = voList.subList(start, end);
        }
        IPage<RequirementVO> resultPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), total);
        resultPage.setRecords(pagedList);
        return resultPage;
    }

}
