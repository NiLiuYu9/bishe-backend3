package com.api.platform.service.impl;

import com.api.platform.entity.UserTag;
import com.api.platform.mapper.UserTagMapper;
import com.api.platform.service.UserTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
