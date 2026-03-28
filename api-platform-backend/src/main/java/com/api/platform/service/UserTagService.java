package com.api.platform.service;

import java.util.List;

public interface UserTagService {

    List<String> getTagsByUserId(Long userId);

    void saveUserTags(Long userId, List<String> tags);

    void addUserTag(Long userId, String tagName);

    void removeUserTag(Long userId, String tagName);

}
