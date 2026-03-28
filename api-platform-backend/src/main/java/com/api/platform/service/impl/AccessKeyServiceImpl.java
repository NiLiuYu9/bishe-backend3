package com.api.platform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.AccessKeyService;
import com.api.platform.common.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccessKeyServiceImpl extends ServiceImpl<UserMapper, User> implements AccessKeyService {

    private static final String SALT = "api_platform";
    
    private static final int LOCK_STRIPES = 64;
    
    private final ConcurrentHashMap<Integer, ReentrantLock> stripedLocks = new ConcurrentHashMap<>();
    
    private ReentrantLock getLock(Long userId) {
        int stripe = (int) (userId % LOCK_STRIPES);
        return stripedLocks.computeIfAbsent(stripe, k -> new ReentrantLock());
    }

    @Override
    public void generateAccessKey(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getAccessKey() == null || user.getAccessKey().isEmpty()) {
            String accessKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(8));
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            updateById(user);
        }
    }

    @Override
    public void regenerateAccessKey(Long userId) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            User user = getById(userId);
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }
            String accessKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(8));
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                    .set(User::getAccessKey, accessKey)
                    .set(User::getSecretKey, secretKey);
            update(updateWrapper);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public User validateAccessKey(String accessKey, String secretKey) {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "AccessKey或SecretKey不能为空");
        }
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccessKey, accessKey)
                .eq(User::getSecretKey, secretKey));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "AccessKey或SecretKey无效");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED, "用户已被禁用");
        }
        return user;
    }

    @Override
    public User getOrGenerateAccessKey(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getAccessKey() == null || user.getAccessKey().isEmpty()) {
            String accessKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + user.getUsername() + RandomUtil.randomNumbers(8));
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            updateById(user);
        }
        return user;
    }

}
