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

/**
 * 缓存预热启动器
 * <p>核心职责：应用启动时自动加载所有已审核通过的API信息到缓存，
 * 包括API详情缓存和路径映射缓存，避免首次请求时缓存未命中导致数据库压力。</p>
 */
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

    /**
     * 应用启动后执行缓存预热
     * <p>加载所有已审核通过的API，批量写入缓存（API详情+路径映射），
     * 同时预加载分类名称和用户名称的映射关系，避免N+1查询。</p>
     *
     * @param args 启动参数
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始缓存预热...");
        
        long startTime = System.currentTimeMillis();
        
        // 查询所有已审核通过的API
        LambdaQueryWrapper<ApiInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiInfo::getStatus, "approved");
        List<ApiInfo> apiInfoList = apiInfoMapper.selectList(queryWrapper);
        
        // 预加载分类名称和用户名称映射，避免逐条查询
        Map<Long, String> typeNameMap = getApiTypeNameMap();
        Map<Long, String> usernameMap = getUsernameMap();
        
        int count = 0;
        for (ApiInfo apiInfo : apiInfoList) {
            try {
                ApiVO apiVO = convertToApiVO(apiInfo, typeNameMap, usernameMap);
                // 写入API详情缓存
                apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
                // 写入路径→ID映射缓存
                apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiInfo.getId());
                count++;
            } catch (Exception e) {
                logger.error("缓存预热失败，API ID: {}", apiInfo.getId(), e);
            }
        }
        
        long endTime = System.currentTimeMillis();
        logger.info("缓存预热完成，共预热 {} 个API，耗时 {} ms", count, endTime - startTime);
    }

    /**
     * 获取分类ID→名称映射
     *
     * @return 分类ID与名称的映射Map
     */
    private Map<Long, String> getApiTypeNameMap() {
        List<ApiType> apiTypes = apiTypeMapper.selectList(null);
        return apiTypes.stream()
                .collect(Collectors.toMap(ApiType::getId, ApiType::getName));
    }

    /**
     * 获取用户ID→用户名映射
     *
     * @return 用户ID与用户名的映射Map
     */
    private Map<Long, String> getUsernameMap() {
        List<User> users = userMapper.selectList(null);
        return users.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    /**
     * ApiInfo实体 → ApiVO转换（含分类名称和用户名填充）
     * <p>与Service层转换不同，此方法额外填充typeName和username，
     * 并解析请求参数和响应参数的JSON字符串为对象列表。</p>
     *
     * @param apiInfo      API信息实体
     * @param typeNameMap  分类ID→名称映射
     * @param usernameMap  用户ID→用户名映射
     * @return 包含完整信息的API详情VO
     */
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
