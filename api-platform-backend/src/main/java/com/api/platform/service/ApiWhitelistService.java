package com.api.platform.service;

import com.api.platform.entity.ApiWhitelist;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ApiWhitelistService extends IService<ApiWhitelist> {

    void addWhitelistUsers(Long apiId, Long operatorId, List<String> usernames);

    void removeWhitelistUser(Long apiId, Long userId, Long operatorId);

    IPage<ApiWhitelist> getWhitelistPage(Long apiId, int pageNum, int pageSize);

    boolean isInWhitelist(Long apiId, Long userId);

    void enableWhitelist(Long apiId, Long operatorId);

    void disableWhitelist(Long apiId, Long operatorId);

}
