package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.dto.QuotaQueryDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.UserApiQuota;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.UserApiQuotaMapper;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.common.ResultCode;
import com.api.platform.vo.UserQuotaVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserApiQuotaServiceImpl extends ServiceImpl<UserApiQuotaMapper, UserApiQuota> implements UserApiQuotaService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Override
    public void addQuota(Long userId, Long apiId, Integer count) {
        UserApiQuota quota = getOne(new LambdaQueryWrapper<UserApiQuota>()
                .eq(UserApiQuota::getUserId, userId)
                .eq(UserApiQuota::getApiId, apiId));
        if (quota == null) {
            quota = new UserApiQuota();
            quota.setUserId(userId);
            quota.setApiId(apiId);
            quota.setTotalCount(count);
            quota.setUsedCount(0);
            quota.setRemainingCount(count);
            save(quota);
        } else {
            quota.setTotalCount(quota.getTotalCount() + count);
            quota.setRemainingCount(quota.getRemainingCount() + count);
            updateById(quota);
        }
    }

    @Override
    public boolean deductQuota(Long userId, Long apiId) {
        UserApiQuota quota = getQuota(userId, apiId);
        if (quota == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无API调用配额");
        }
        if (quota.getRemainingCount() <= 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "API调用配额已用尽");
        }
        quota.setUsedCount(quota.getUsedCount() + 1);
        quota.setRemainingCount(quota.getRemainingCount() - 1);
        updateById(quota);
        return true;
    }

    @Override
    public UserApiQuota getQuota(Long userId, Long apiId) {
        return getOne(new LambdaQueryWrapper<UserApiQuota>()
                .eq(UserApiQuota::getUserId, userId)
                .eq(UserApiQuota::getApiId, apiId));
    }

    @Override
    public List<UserApiQuota> getUserQuotas(Long userId) {
        return list(new LambdaQueryWrapper<UserApiQuota>()
                .eq(UserApiQuota::getUserId, userId)
                .orderByDesc(UserApiQuota::getCreateTime));
    }

    @Override
    public boolean hasQuota(Long userId, Long apiId) {
        UserApiQuota quota = getQuota(userId, apiId);
        return quota != null && quota.getRemainingCount() > 0;
    }

    @Override
    public IPage<UserQuotaVO> pageUserQuotas(QuotaQueryDTO queryDTO) {
        Page<UserApiQuota> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<UserApiQuota> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getUserId() != null, UserApiQuota::getUserId, queryDTO.getUserId());
        
        if (StrUtil.isNotBlank(queryDTO.getApiName())) {
            List<ApiInfo> apiInfos = apiInfoMapper.selectList(new LambdaQueryWrapper<ApiInfo>()
                    .like(ApiInfo::getName, queryDTO.getApiName()));
            if (apiInfos.isEmpty()) {
                IPage<UserQuotaVO> emptyPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), 0);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            List<Long> apiIds = apiInfos.stream().map(ApiInfo::getId).collect(Collectors.toList());
            queryWrapper.in(UserApiQuota::getApiId, apiIds);
        }
        
        queryWrapper.orderByDesc(UserApiQuota::getCreateTime);
        IPage<UserApiQuota> quotaPage = page(page, queryWrapper);
        
        if (quotaPage.getRecords().isEmpty()) {
            IPage<UserQuotaVO> emptyPage = new Page<>(quotaPage.getCurrent(), quotaPage.getSize(), quotaPage.getTotal());
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        
        Map<Long, String> apiNameMap = getApiNameMap(quotaPage.getRecords());
        
        IPage<UserQuotaVO> voPage = new Page<>(quotaPage.getCurrent(), quotaPage.getSize(), quotaPage.getTotal());
        List<UserQuotaVO> voList = quotaPage.getRecords().stream().map(quota -> {
            UserQuotaVO vo = new UserQuotaVO();
            vo.setId(quota.getId());
            vo.setApiId(quota.getApiId());
            vo.setTotalCount(quota.getTotalCount());
            vo.setUsedCount(quota.getUsedCount());
            vo.setRemainingCount(quota.getRemainingCount());
            vo.setCreateTime(quota.getCreateTime());
            vo.setUpdateTime(quota.getUpdateTime());
            vo.setApiName(apiNameMap.get(quota.getApiId()));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private Map<Long, String> getApiNameMap(List<UserApiQuota> quotas) {
        List<Long> apiIds = quotas.stream()
                .map(UserApiQuota::getApiId)
                .distinct()
                .collect(Collectors.toList());
        if (apiIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return apiInfoMapper.selectBatchIds(apiIds).stream()
                .collect(Collectors.toMap(ApiInfo::getId, ApiInfo::getName));
    }

}
