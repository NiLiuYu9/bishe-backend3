package com.api.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiWhitelist;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiWhitelistMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiWhitelistService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API白名单服务实现 —— 管理API的访问白名单
 *
 * 白名单模式：当API启用白名单后，只有白名单中的用户才能调用该API
 * 适用于内部API或付费后限制访问的场景
 */
@Service
public class ApiWhitelistServiceImpl extends ServiceImpl<ApiWhitelistMapper, ApiWhitelist> implements ApiWhitelistService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWhitelistUsers(Long apiId, Long operatorId, List<String> usernames) {
        if (CollUtil.isEmpty(usernames)) {
            throw new BusinessException("白名单用户名列表不能为空");
        }
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(operatorId)) {
            throw new BusinessException("无权限操作该API的白名单");
        }
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getUsername, usernames));
        if (users.isEmpty()) {
            throw new BusinessException("未找到有效的用户");
        }
        List<Long> existingUserIds = this.baseMapper.selectList(new LambdaQueryWrapper<ApiWhitelist>()
                .eq(ApiWhitelist::getApiId, apiId)
                .select(ApiWhitelist::getUserId))
                .stream()
                .map(ApiWhitelist::getUserId)
                .collect(Collectors.toList());
        List<ApiWhitelist> toAdd = new ArrayList<>();
        for (User user : users) {
            if (!existingUserIds.contains(user.getId())) {
                ApiWhitelist whitelist = new ApiWhitelist();
                whitelist.setApiId(apiId);
                whitelist.setUserId(user.getId());
                toAdd.add(whitelist);
            }
        }
        if (!toAdd.isEmpty()) {
            saveBatch(toAdd);
        }
        if (apiInfo.getWhitelistEnabled() == null || apiInfo.getWhitelistEnabled() != 1) {
            apiInfo.setWhitelistEnabled(1);
            apiInfoMapper.updateById(apiInfo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWhitelistUser(Long apiId, Long userId, Long operatorId) {
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(operatorId)) {
            throw new BusinessException("无权限操作该API的白名单");
        }
        remove(new LambdaQueryWrapper<ApiWhitelist>()
                .eq(ApiWhitelist::getApiId, apiId)
                .eq(ApiWhitelist::getUserId, userId));
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<ApiWhitelist>()
                .eq(ApiWhitelist::getApiId, apiId));
        if (count == 0) {
            apiInfo.setWhitelistEnabled(0);
            apiInfoMapper.updateById(apiInfo);
        }
    }

    @Override
    public IPage<ApiWhitelist> getWhitelistPage(Long apiId, int pageNum, int pageSize) {
        Page<ApiWhitelist> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApiWhitelist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiWhitelist::getApiId, apiId)
                .orderByDesc(ApiWhitelist::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public boolean isInWhitelist(Long apiId, Long userId) {
        if (apiId == null || userId == null) {
            return false;
        }
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<ApiWhitelist>()
                .eq(ApiWhitelist::getApiId, apiId)
                .eq(ApiWhitelist::getUserId, userId));
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableWhitelist(Long apiId, Long operatorId) {
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(operatorId)) {
            throw new BusinessException("无权限操作该API的白名单");
        }
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<ApiWhitelist>()
                .eq(ApiWhitelist::getApiId, apiId));
        if (count == 0) {
            throw new BusinessException("开启白名单前必须先添加白名单用户");
        }
        apiInfo.setWhitelistEnabled(1);
        apiInfoMapper.updateById(apiInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableWhitelist(Long apiId, Long operatorId) {
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(operatorId)) {
            throw new BusinessException("无权限操作该API的白名单");
        }
        apiInfo.setWhitelistEnabled(0);
        apiInfoMapper.updateById(apiInfo);
    }

}
