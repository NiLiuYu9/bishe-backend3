package com.api.platform.service;

import com.api.platform.dto.QuotaQueryDTO;
import com.api.platform.entity.UserApiQuota;
import com.api.platform.vo.UserQuotaVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户配额服务接口 —— 定义用户API调用配额相关的业务操作
 *
 * 所属业务模块：配额管理模块
 * 包括配额增加、配额扣减、配额查询、配额检查等功能
 * 实现类为 UserApiQuotaServiceImpl
 */
public interface UserApiQuotaService extends IService<UserApiQuota> {

    /**
     * 增加用户配额
     *
     * 用户购买API后增加对应调用配额
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @param count  增加的配额数量
     */
    void addQuota(Long userId, Long apiId, Integer count);

    /**
     * 扣减用户配额
     *
     * 用户调用API时扣减一次配额
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return boolean 扣减成功返回 true，配额不足返回 false
     */
    boolean deductQuota(Long userId, Long apiId);

    /**
     * 获取用户对指定API的配额信息
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return UserApiQuota 配额实体，不存在返回 null
     */
    UserApiQuota getQuota(Long userId, Long apiId);

    /**
     * 获取用户所有API的配额列表
     *
     * @param userId 用户 ID
     * @return List<UserApiQuota> 配额列表
     */
    List<UserApiQuota> getUserQuotas(Long userId);

    /**
     * 检查用户是否有剩余配额
     *
     * @param userId 用户 ID
     * @param apiId  API ID
     * @return boolean 有剩余配额返回 true
     */
    boolean hasQuota(Long userId, Long apiId);

    /**
     * 分页查询用户配额列表（管理端）
     *
     * @param queryDTO 查询条件（用户名、API名称、分页参数）
     * @return IPage<UserQuotaVO> 分页配额列表
     */
    IPage<UserQuotaVO> pageUserQuotas(QuotaQueryDTO queryDTO);

}
