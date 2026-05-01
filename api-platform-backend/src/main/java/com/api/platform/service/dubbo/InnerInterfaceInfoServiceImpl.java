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

/**
 * Dubbo服务实现 - 内部接口信息服务
 * <p>核心职责：按请求路径/方法或接口ID查询接口信息，供网关路由调用。
 * 网关在转发请求前，需要根据请求路径和HTTP方法定位具体的API接口，
 * 获取目标URL、调用限制等信息，本服务提供此查询能力，
 * 并通过缓存机制提升查询性能。</p>
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Resource
    private ApiCacheService apiCacheService;

    /**
     * 根据请求路径和HTTP方法查询接口信息
     * <p>查询流程：先查缓存（路径→ID映射 → 接口详情缓存），缓存未命中再查数据库，
     * 查到后回写缓存，避免后续重复查库。</p>
     *
     * @param path   API请求路径（如 /api/weather）
     * @param method HTTP方法（如 GET、POST）
     * @return 接口信息VO；参数为空或接口不存在时返回null
     */
    @Override
    public InterfaceInfoVO getInterfaceInfo(String path, String method) {
        if (path == null || method == null) {
            return null;
        }

        // 优先从缓存获取路径对应的API ID
        Long apiId = apiCacheService.getApiIdByPath(path, method);
        
        if (apiId != null) {
            // 缓存命中路径映射，进一步获取接口详情
            ApiVO apiVO = getApiDetailById(apiId);
            if (apiVO != null) {
                return convertToInterfaceInfoVO(apiVO);
            }
        }

        // 缓存未命中，从数据库按路径+方法精确查询
        ApiInfo apiInfo = apiInfoMapper.selectOne(new LambdaQueryWrapper<ApiInfo>()
                .eq(ApiInfo::getEndpoint, path)
                .eq(ApiInfo::getMethod, method));
        
        if (apiInfo == null) {
            return null;
        }

        // 回写缓存：路径映射 + 接口详情
        apiCacheService.cachePathMapping(path, method, apiInfo.getId());
        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
        
        return convertToVO(apiInfo);
    }

    /**
     * 根据接口ID查询接口信息
     * <p>查询流程：先检查空值缓存（防止缓存穿透），再查接口详情缓存，
     * 缓存未命中查数据库，查到后回写缓存。</p>
     *
     * @param id 接口ID
     * @return 接口信息VO；id为空或接口不存在时返回null
     */
    @Override
    public InterfaceInfoVO getInterfaceInfoById(Long id) {
        if (id == null) {
            return null;
        }

        // 防缓存穿透：如果之前查过且不存在，已缓存空值标记
        if (apiCacheService.isNullValueCached(id)) {
            return null;
        }

        // 尝试从缓存获取接口详情
        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(id);
        if (cachedVO != null) {
            return convertToInterfaceInfoVO(cachedVO);
        }

        // 缓存未命中，查数据库
        ApiInfo apiInfo = apiInfoMapper.selectById(id);
        if (apiInfo == null) {
            // 缓存空值标记，防止缓存穿透
            apiCacheService.cacheNullValue(id);
            return null;
        }

        // 回写缓存：接口详情 + 路径映射
        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(id, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);
        
        return convertToVO(apiInfo);
    }

    /**
     * 根据接口ID获取API详情（带缓存）
     * <p>内部方法，供getInterfaceInfo在路径映射命中后调用，
     * 同样遵循"缓存优先、回写缓存"的策略。</p>
     *
     * @param id 接口ID
     * @return API详情VO；id对应接口不存在时返回null
     */
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

    /**
     * ApiInfo实体 → InterfaceInfoVO转换（网关路由所需字段）
     *
     * @param apiInfo API信息实体
     * @return 网关路由所需的接口信息VO
     */
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
        vo.setCallLimit(apiInfo.getCallLimit());
        return vo;
    }

    /**
     * ApiVO → InterfaceInfoVO转换（缓存对象转网关VO）
     *
     * @param apiVO 缓存中的API详情VO
     * @return 网关路由所需的接口信息VO
     */
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
        vo.setCallLimit(apiVO.getCallLimit());
        return vo;
    }

    /**
     * ApiInfo实体 → ApiVO转换（完整字段映射，用于缓存存储）
     *
     * @param apiInfo API信息实体
     * @return 包含完整字段的API详情VO
     */
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
