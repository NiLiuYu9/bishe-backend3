package com.api.platform.service.impl;

import com.api.platform.entity.UserTag;
import com.api.platform.mapper.UserTagMapper;
import com.api.platform.service.UserTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户标签服务实现 —— 管理用户的技能标签
 *
 * 用户标签用于智能匹配：系统根据用户标签与需求标签的相似度推荐需求
 * 每个用户可以有多个标签，标签保存后全量替换
 */
@Service
public class UserTagServiceImpl implements UserTagService {

    @Autowired
    private UserTagMapper userTagMapper;

    @Override
    public List<String> getTagsByUserId(Long userId) {
        return userTagMapper.selectTagNamesByUserId(userId);
    }

    @Override
    @Transactional
    public void saveUserTags(Long userId, List<String> tags) {
        userTagMapper.deleteByUserId(userId);
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    UserTag userTag = new UserTag();
                    userTag.setUserId(userId);
                    userTag.setTagName(tagName.trim());
                    userTagMapper.insert(userTag);
                }
            }
        }
    }

    @Override
    public void addUserTag(Long userId, String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return;
        }
        String trimmedTag = tagName.trim();
        Integer exists = userTagMapper.existsByUserIdAndTagName(userId, trimmedTag);
        if (exists != null && exists > 0) {
            return;
        }
        UserTag userTag = new UserTag();
        userTag.setUserId(userId);
        userTag.setTagName(trimmedTag);
        userTagMapper.insert(userTag);
    }

    @Override
    public void removeUserTag(Long userId, String tagName) {
        userTagMapper.deleteByUserIdAndTagName(userId, tagName);
    }

}
