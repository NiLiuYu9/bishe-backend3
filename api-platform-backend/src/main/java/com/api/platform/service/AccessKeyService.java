package com.api.platform.service;

import com.api.platform.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AccessKeyService extends IService<User> {

    void generateAccessKey(Long userId);

    void regenerateAccessKey(Long userId);

    User validateAccessKey(String accessKey, String secretKey);

    User getOrGenerateAccessKey(Long userId);

}
