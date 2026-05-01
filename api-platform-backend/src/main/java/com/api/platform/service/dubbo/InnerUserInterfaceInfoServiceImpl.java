package com.api.platform.service.dubbo;

import com.api.platform.common.service.InnerUserInterfaceInfoService;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.UserApiQuota;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.UserApiQuotaMapper;
import com.api.platform.service.ApiInvokeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * Dubbo服务实现 - 内部用户接口调用信息服务
 * <p>核心职责：更新API调用次数和检查用户调用配额，供网关统计调用。
 * 网关在成功转发API请求后，调用本服务更新调用计数；
 * 在转发前，调用本服务检查用户是否还有剩余调用配额。</p>
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserApiQuotaMapper userApiQuotaMapper;

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Resource
    private ApiInvokeService apiInvokeService;

    /**
     * 更新接口调用次数（用户配额+API统计）
     * <p>在一次成功调用后执行：原子性地递增用户已用次数、递减剩余次数（最低为0），
     * 同时递增API的总调用次数和成功次数，并记录调用日志。</p>
     *
     * @param interfaceInfoId 接口ID
     * @param userId          调用用户ID
     */
    @Override
    public void invokeCount(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId == null || userId == null) {
            return;
        }
        
        // 原子更新用户配额：已用+1，剩余-1（不低于0）
        LambdaUpdateWrapper<UserApiQuota> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserApiQuota::getUserId, userId)
                .eq(UserApiQuota::getApiId, interfaceInfoId)
                .setSql("used_count = used_count + 1, remaining_count = GREATEST(remaining_count - 1, 0)");
        userApiQuotaMapper.update(null, updateWrapper);

        // 原子更新API统计：总调用次数+1，成功次数+1
        LambdaUpdateWrapper<ApiInfo> apiUpdateWrapper = new LambdaUpdateWrapper<>();
        apiUpdateWrapper.eq(ApiInfo::getId, interfaceInfoId)
                .setSql("invoke_count = invoke_count + 1, success_count = success_count + 1");
        apiInfoMapper.update(null, apiUpdateWrapper);

        // 记录调用日志（用于统计报表）
        ApiInfo apiInfo = apiInfoMapper.selectById(interfaceInfoId);
        if (apiInfo != null) {
            apiInvokeService.recordInvoke(interfaceInfoId, apiInfo.getName(), userId, apiInfo.getUserId(), true);
        }
    }

    /**
     * 检查用户是否还有指定接口的调用配额
     * <p>网关在转发请求前调用此方法，判断用户剩余调用次数是否大于0。</p>
     *
     * @param userId          用户ID
     * @param interfaceInfoId 接口ID
     * @return true-仍有配额可调用；false-无配额或未分配配额
     */
    @Override
    public boolean hasQuota(Long userId, Long interfaceInfoId) {
        if (userId == null || interfaceInfoId == null) {
            return false;
        }
        UserApiQuota quota = userApiQuotaMapper.selectOne(new LambdaQueryWrapper<UserApiQuota>()
                .eq(UserApiQuota::getUserId, userId)
                .eq(UserApiQuota::getApiId, interfaceInfoId));
        return quota != null && quota.getRemainingCount() != null && quota.getRemainingCount() > 0;
    }
}
