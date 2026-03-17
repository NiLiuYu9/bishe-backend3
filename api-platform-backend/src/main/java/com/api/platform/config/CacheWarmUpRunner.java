package com.api.platform.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.api.platform.dto.ApiParamDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiType;
import com.api.platform.entity.User;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiTypeMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiCacheService;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CacheWarmUpRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CacheWarmUpRunner.class);

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private ApiTypeMapper apiTypeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApiCacheService apiCacheService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始缓存预热...");
        
        long startTime = System.currentTimeMillis();
        
        LambdaQueryWrapper<ApiInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInfo::getStatus, "approved");
        List<ApiInfo> apiInfoList = apiInfoMapper.selectList(queryWrapper);
        
        Map<Long, String> typeNameMap = getApiTypeNameMap();
        Map<Long, String> usernameMap = getUsernameMap();
        
        int count = 0;
        for (ApiInfo apiInfo : apiInfoList) {
            try {
                ApiVO apiVO = convertToApiVO(apiInfo, typeNameMap, usernameMap);
                apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
                apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiInfo.getId());
                count++;
            } catch (Exception e) {
                logger.error("缓存预热失败，API ID: {}", apiInfo.getId(), e);
            }
        }
        
        long endTime = System.currentTimeMillis();
        logger.info("缓存预热完成，共预热 {} 个API，耗时 {} ms", count, endTime - startTime);
    }

    private Map<Long, String> getApiTypeNameMap() {
        List<ApiType> apiTypes = apiTypeMapper.selectList(null);
        return apiTypes.stream()
                .collect(Collectors.toMap(ApiType::getId, ApiType::getName));
    }

    private Map<Long, String> getUsernameMap() {
        List<User> users = userMapper.selectList(null);
        return users.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    private ApiVO convertToApiVO(ApiInfo apiInfo, Map<Long, String> typeNameMap, Map<Long, String> usernameMap) {
        if (apiInfo == null) {
            return null;
        }
        ApiVO vo = new ApiVO();
        vo.setId(apiInfo.getId());
        vo.setName(apiInfo.getName());
        vo.setDescription(apiInfo.getDescription());
        vo.setTypeName(typeNameMap.get(apiInfo.getTypeId()));
        vo.setTypeId(apiInfo.getTypeId());
        vo.setUsername(usernameMap.get(apiInfo.getUserId()));
        vo.setUserId(apiInfo.getUserId());
        vo.setMethod(apiInfo.getMethod());
        vo.setEndpoint(apiInfo.getEndpoint());
        vo.setTargetUrl(apiInfo.getTargetUrl());
        if (StrUtil.isNotBlank(apiInfo.getRequestParams())) {
            vo.setRequestParams(JSONUtil.toList(apiInfo.getRequestParams(), ApiParamDTO.class));
        }
        if (StrUtil.isNotBlank(apiInfo.getResponseParams())) {
            vo.setResponseParams(JSONUtil.toList(apiInfo.getResponseParams(), ApiParamDTO.class));
        }
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
        vo.setIsFavorited(false);
        return vo;
    }
}
