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

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserApiQuotaMapper userApiQuotaMapper;

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Resource
    private ApiInvokeService apiInvokeService;

    @Override
    public void invokeCount(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId == null || userId == null) {
            return;
        }
        
        LambdaUpdateWrapper<UserApiQuota> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserApiQuota::getUserId, userId)
                .eq(UserApiQuota::getApiId, interfaceInfoId)
                .setSql("used_count = used_count + 1, remaining_count = GREATEST(remaining_count - 1, 0)");
        userApiQuotaMapper.update(null, updateWrapper);

        LambdaUpdateWrapper<ApiInfo> apiUpdateWrapper = new LambdaUpdateWrapper<>();
        apiUpdateWrapper.eq(ApiInfo::getId, interfaceInfoId)
                .setSql("invoke_count = invoke_count + 1, success_count = success_count + 1");
        apiInfoMapper.update(null, apiUpdateWrapper);

        ApiInfo apiInfo = apiInfoMapper.selectById(interfaceInfoId);
        if (apiInfo != null) {
            apiInvokeService.recordInvoke(interfaceInfoId, apiInfo.getName(), userId, apiInfo.getUserId(), true);
        }
    }

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
