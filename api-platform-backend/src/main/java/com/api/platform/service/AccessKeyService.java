package com.api.platform.service;

import com.api.platform.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 密钥服务接口 —— 定义用户AK/SK密钥相关的业务操作
 *
 * 所属业务模块：密钥管理模块
 * 包括密钥生成、密钥重新生成、密钥验证等功能
 * AK/SK 用于API调用时的身份认证和签名验证
 * 实现类为 AccessKeyServiceImpl
 */
public interface AccessKeyService extends IService<User> {

    /**
     * 生成密钥
     *
     * 为指定用户生成唯一的 AccessKey 和 SecretKey
     *
     * @param userId 用户 ID
     */
    void generateAccessKey(Long userId);

    /**
     * 重新生成密钥
     *
     * 废弃旧密钥，生成新的 AccessKey 和 SecretKey
     *
     * @param userId 用户 ID
     */
    void regenerateAccessKey(Long userId);

    /**
     * 验证密钥有效性
     *
     * 根据 AccessKey 和 SecretKey 查找并验证用户身份
     *
     * @param accessKey AccessKey
     * @param secretKey SecretKey
     * @return User 验证通过返回用户实体，失败返回 null
     */
    User validateAccessKey(String accessKey, String secretKey);

    /**
     * 获取或生成密钥
     *
     * 若用户已有密钥则直接返回，否则生成新密钥
     *
     * @param userId 用户 ID
     * @return User 包含密钥信息的用户实体
     */
    User getOrGenerateAccessKey(Long userId);

}
