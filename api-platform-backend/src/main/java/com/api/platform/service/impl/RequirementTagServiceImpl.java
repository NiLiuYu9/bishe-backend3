package com.api.platform.service.impl;

import com.api.platform.entity.RequirementTag;
import com.api.platform.mapper.RequirementTagMapper;
import com.api.platform.service.RequirementTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 需求标签服务实现 —— 管理需求的技术标签
 *
 * 需求标签用于描述需求所需的技术栈，如"Java"、"Vue"、"Python"等
 * 标签与需求为多对多关系，通过requirement_tag表关联
 */
@Service
public class RequirementTagServiceImpl implements RequirementTagService {

    @Autowired
    private RequirementTagMapper requirementTagMapper;

    @Override
    public List<String> getTagsByRequirementId(Long requirementId) {
        return requirementTagMapper.selectTagNamesByRequirementId(requirementId);
    }

    @Override
    @Transactional
    public void saveRequirementTags(Long requirementId, List<String> tags) {
        requirementTagMapper.deleteByRequirementId(requirementId);
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    RequirementTag requirementTag = new RequirementTag();
                    requirementTag.setRequirementId(requirementId);
                    requirementTag.setTagName(tagName.trim());
                    requirementTagMapper.insert(requirementTag);
                }
            }
        }
    }

    @Override
    public Map<Long, List<String>> getTagsByRequirementIds(List<Long> requirementIds) {
        if (requirementIds == null || requirementIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> results = requirementTagMapper.selectTagsByRequirementIds(requirementIds);
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (Map<String, Object> row : results) {
            Long requirementId = ((Number) row.get("requirementId")).longValue();
            String tagName = (String) row.get("tagName");
            tagMap.computeIfAbsent(requirementId, k -> new ArrayList<>()).add(tagName);
        }
        return tagMap;
    }

}
