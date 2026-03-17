package com.api.platform.service.dubbo;

import com.api.platform.common.service.InnerInterfaceInfoService;
import com.api.platform.common.vo.InterfaceInfoVO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.service.ApiCacheService;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Resource
    private ApiCacheService apiCacheService;

    @Override
    public InterfaceInfoVO getInterfaceInfo(String path, String method) {
        if (path == null || method == null) {
            return null;
        }

        Long apiId = apiCacheService.getApiIdByPath(path, method);
        
        if (apiId != null) {
            ApiVO apiVO = getApiDetailById(apiId);
            if (apiVO != null) {
                return convertToInterfaceInfoVO(apiVO);
            }
        }

        ApiInfo apiInfo = apiInfoMapper.selectOne(new LambdaQueryWrapper<ApiInfo>()
                .eq(ApiInfo::getEndpoint, path)
                .eq(ApiInfo::getMethod, method));
        
        if (apiInfo == null) {
            return null;
        }

        apiCacheService.cachePathMapping(path, method, apiInfo.getId());
        
        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
        
        return convertToVO(apiInfo);
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoById(Long id) {
        if (id == null) {
            return null;
        }

        if (apiCacheService.isNullValueCached(id)) {
            return null;
        }

        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(id);
        if (cachedVO != null) {
            return convertToInterfaceInfoVO(cachedVO);
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(id);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(id);
            return null;
        }

        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(id, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);
        
        return convertToVO(apiInfo);
    }

    private ApiVO getApiDetailById(Long id) {
        if (apiCacheService.isNullValueCached(id)) {
            return null;
        }

        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(id);
        if (cachedVO != null) {
            return cachedVO;
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(id);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(id);
            return null;
        }

        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(id, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);
        
        return apiVO;
    }

    private InterfaceInfoVO convertToVO(ApiInfo apiInfo) {
        if (apiInfo == null) {
            return null;
        }
        InterfaceInfoVO vo = new InterfaceInfoVO();
        vo.setId(apiInfo.getId());
        vo.setPath(apiInfo.getEndpoint());
        vo.setMethod(apiInfo.getMethod());
        vo.setStatus(apiInfo.getStatus());
        vo.setTargetUrl(apiInfo.getTargetUrl());
        return vo;
    }

    private InterfaceInfoVO convertToInterfaceInfoVO(ApiVO apiVO) {
        if (apiVO == null) {
            return null;
        }
        InterfaceInfoVO vo = new InterfaceInfoVO();
        vo.setId(apiVO.getId());
        vo.setPath(apiVO.getEndpoint());
        vo.setMethod(apiVO.getMethod());
        vo.setStatus(apiVO.getStatus());
        vo.setTargetUrl(apiVO.getTargetUrl());
        return vo;
    }

    private ApiVO convertToApiVO(ApiInfo apiInfo) {
        if (apiInfo == null) {
            return null;
        }
        ApiVO vo = new ApiVO();
        vo.setId(apiInfo.getId());
        vo.setName(apiInfo.getName());
        vo.setDescription(apiInfo.getDescription());
        vo.setTypeId(apiInfo.getTypeId());
        vo.setUserId(apiInfo.getUserId());
        vo.setMethod(apiInfo.getMethod());
        vo.setEndpoint(apiInfo.getEndpoint());
        vo.setTargetUrl(apiInfo.getTargetUrl());
        vo.setPrice(apiInfo.getPrice());
        vo.setPriceUnit(apiInfo.getPriceUnit());
        vo.setCallLimit(apiInfo.getCallLimit());
        vo.setStatus(apiInfo.getStatus());
        vo.setCreateTime(apiInfo.getCreateTime());
        vo.setUpdateTime(apiInfo.getUpdateTime());
        vo.setDocUrl(apiInfo.getDocUrl());
        vo.setRating(apiInfo.getRating());
        vo.setInvokeCount(apiInfo.getInvokeCount());
        vo.setSuccessCount(apiInfo.getSuccessCount());
        vo.setFailCount(apiInfo.getFailCount());
        return vo;
    }
}
