package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.api.platform.dto.ApiParamDTO;
import com.api.platform.entity.ApiFavorite;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiFavoriteMapper;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.service.ApiFavoriteService;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ApiFavoriteServiceImpl extends ServiceImpl<ApiFavoriteMapper, ApiFavorite> implements ApiFavoriteService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Override
    public void addFavorite(Long userId, Long apiId) {
        if (apiInfoMapper.selectById(apiId) == null) {
            throw new BusinessException("API不存在");
        }
        if (isFavorited(userId, apiId)) {
            throw new BusinessException("已收藏该API");
        }
        ApiFavorite favorite = new ApiFavorite();
        favorite.setUserId(userId);
        favorite.setApiId(apiId);
        save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long apiId) {
        LambdaQueryWrapper<ApiFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiFavorite::getUserId, userId)
                .eq(ApiFavorite::getApiId, apiId);
        remove(queryWrapper);
    }

    @Override
    public boolean isFavorited(Long userId, Long apiId) {
        if (userId == null || apiId == null) {
            return false;
        }
        LambdaQueryWrapper<ApiFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiFavorite::getUserId, userId)
                .eq(ApiFavorite::getApiId, apiId);
        return count(queryWrapper) > 0;
    }

    @Override
    public List<Long> getUserFavoriteApiIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectUserFavoriteApiIds(userId);
    }

    @Override
    public IPage<ApiVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<ApiVO> page = new Page<>(pageNum, pageSize);
        IPage<ApiVO> result = baseMapper.selectUserFavoriteApis(page, userId);
        result.getRecords().forEach(api -> {
            api.setIsFavorited(true);
            if (StrUtil.isNotBlank(api.getRequestParamsJson())) {
                api.setRequestParams(JSONUtil.toList(api.getRequestParamsJson(), ApiParamDTO.class));
            }
            if (StrUtil.isNotBlank(api.getResponseParamsJson())) {
                api.setResponseParams(JSONUtil.toList(api.getResponseParamsJson(), ApiParamDTO.class));
            }
        });
        return result;
    }

}
