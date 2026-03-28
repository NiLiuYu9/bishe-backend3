package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.dto.ApiCreateDTO;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiStatusDTO;
import com.api.platform.dto.AuditApiDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiType;
import com.api.platform.entity.User;
import com.api.platform.vo.ApiVO;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiTypeMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.ApiInfoService;
import com.api.platform.service.ApiFavoriteService;
import com.api.platform.exception.BusinessException;
import com.api.platform.utils.VoConverterUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo> implements ApiInfoService {

    @Autowired
    private ApiTypeMapper apiTypeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApiFavoriteService apiFavoriteService;

    @Autowired
    private ApiCacheService apiCacheService;

    @Override
    public IPage<ApiVO> getApis(ApiQueryDTO queryDTO) {
        return getApis(queryDTO, null);
    }

    @Override
    public IPage<ApiVO> getApis(ApiQueryDTO queryDTO, Long currentUserId) {
        Page<ApiInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<ApiInfo> queryWrapper = buildQueryWrapper(queryDTO);
        IPage<ApiInfo> apiInfoPage = page(page, queryWrapper);
        
        if (apiInfoPage.getRecords().isEmpty()) {
            IPage<ApiVO> emptyPage = new Page<>(apiInfoPage.getCurrent(), apiInfoPage.getSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        
        Map<Long, String> typeNameMap = getTypeNameMap(apiInfoPage.getRecords());
        Map<Long, String> usernameMap = getUsernameMap(apiInfoPage.getRecords());
        
        Set<Long> favoritedApiIds = new HashSet<>();
        if (currentUserId != null) {
            favoritedApiIds = new HashSet<>(apiFavoriteService.getUserFavoriteApiIds(currentUserId));
        }
        
        final Set<Long> finalFavoritedApiIds = favoritedApiIds;
        IPage<ApiVO> apiVOPage = new Page<>(apiInfoPage.getCurrent(), apiInfoPage.getSize(), apiInfoPage.getTotal());
        apiVOPage.setRecords(VoConverterUtils.convertToApiVOList(apiInfoPage.getRecords(), typeNameMap, usernameMap, finalFavoritedApiIds));
        return apiVOPage;
    }

    @Override
    public ApiVO getApiDetailById(Long id) {
        return getApiDetailById(id, null);
    }

    @Override
    public ApiVO getApiDetailById(Long id, Long currentUserId) {
        if (apiCacheService.isNullValueCached(id)) {
            return null;
        }

        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(id);
        if (cachedVO != null) {
            if (currentUserId != null) {
                cachedVO.setIsFavorited(apiFavoriteService.isFavorited(currentUserId, id));
            } else {
                cachedVO.setIsFavorited(false);
            }
            return cachedVO;
        }

        ApiInfo apiInfo = getById(id);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(id);
            return null;
        }
        ApiType apiType = apiTypeMapper.selectById(apiInfo.getTypeId());
        User user = userMapper.selectById(apiInfo.getUserId());
        ApiVO apiVO = VoConverterUtils.convertToApiVO(apiInfo, apiType, user);
        if (currentUserId != null) {
            apiVO.setIsFavorited(apiFavoriteService.isFavorited(currentUserId, id));
        } else {
            apiVO.setIsFavorited(false);
        }

        apiCacheService.cacheApiDetail(id, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);

        return apiVO;
    }

    @Override
    public ApiVO createApi(Long userId, ApiCreateDTO createDTO) {
        ApiInfo apiInfo = new ApiInfo();
        copyCreateDtoToEntity(createDTO, apiInfo);
        apiInfo.setUserId(userId);
        apiInfo.setStatus("pending");
        apiInfo.setRating(BigDecimal.ZERO);
        apiInfo.setInvokeCount(0L);
        apiInfo.setSuccessCount(0L);
        apiInfo.setFailCount(0L);
        save(apiInfo);
        
        ApiType apiType = apiTypeMapper.selectById(apiInfo.getTypeId());
        User user = userMapper.selectById(userId);
        ApiVO apiVO = VoConverterUtils.convertToApiVO(apiInfo, apiType, user);
        
        apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiInfo.getId());
        apiCacheService.clearListCache();
        
        return apiVO;
    }

    @Override
    public ApiVO updateApi(Long userId, Long apiId, ApiCreateDTO updateDTO) {
        ApiInfo oldApiInfo = getById(apiId);
        if (oldApiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!oldApiInfo.getUserId().equals(userId)) {
            throw new BusinessException("无权限编辑该API");
        }
        
        String oldEndpoint = oldApiInfo.getEndpoint();
        String oldMethod = oldApiInfo.getMethod();
        Integer oldCallLimit = oldApiInfo.getCallLimit();
        
        copyCreateDtoToEntity(updateDTO, oldApiInfo);
        oldApiInfo.setStatus("pending");
        updateById(oldApiInfo);
        
        ApiType apiType = apiTypeMapper.selectById(oldApiInfo.getTypeId());
        User user = userMapper.selectById(userId);
        ApiVO apiVO = VoConverterUtils.convertToApiVO(oldApiInfo, apiType, user);
        
        apiCacheService.cacheApiDetail(apiId, apiVO);
        
        Integer newCallLimit = oldApiInfo.getCallLimit();
        if (oldCallLimit == null && newCallLimit != null || 
            oldCallLimit != null && !oldCallLimit.equals(newCallLimit)) {
            apiCacheService.clearRateLimitCache(apiId);
        }
        
        if (!Objects.equals(oldEndpoint, oldApiInfo.getEndpoint()) || !Objects.equals(oldMethod, oldApiInfo.getMethod())) {
            apiCacheService.deletePathMapping(oldEndpoint, oldMethod);
        }
        apiCacheService.cachePathMapping(oldApiInfo.getEndpoint(), oldApiInfo.getMethod(), apiId);
        apiCacheService.clearListCache();
        
        return apiVO;
    }

    @Override
    public void updateApiStatus(Long userId, Long apiId, ApiStatusDTO statusDTO) {
        ApiInfo apiInfo = getById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该API");
        }
        String currentStatus = apiInfo.getStatus();
        String newStatus = statusDTO.getStatus();
        
        validateStatusTransition(currentStatus, newStatus, true);
        
        LambdaUpdateWrapper<ApiInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApiInfo::getId, apiId)
                .set(ApiInfo::getStatus, newStatus);
        update(updateWrapper);

        apiInfo.setStatus(newStatus);
        ApiVO apiVO = getApiVOFromEntity(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.clearListCache();
    }

    @Override
    public void auditApi(Long apiId, AuditApiDTO auditApiDTO) {
        ApiInfo apiInfo = getById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        String currentStatus = apiInfo.getStatus();
        String newStatus = auditApiDTO.getStatus();
        
        validateAuditStatusTransition(currentStatus, newStatus);
        
        LambdaUpdateWrapper<ApiInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApiInfo::getId, apiId)
                .set(ApiInfo::getStatus, newStatus);
        update(updateWrapper);

        apiInfo.setStatus(newStatus);
        ApiVO apiVO = getApiVOFromEntity(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.clearListCache();
    }

    private ApiVO getApiVOFromEntity(ApiInfo apiInfo) {
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
        vo.setWhitelistEnabled(apiInfo.getWhitelistEnabled() != null ? apiInfo.getWhitelistEnabled() : 0);
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

    private LambdaQueryWrapper<ApiInfo> buildQueryWrapper(ApiQueryDTO queryDTO) {
        LambdaQueryWrapper<ApiInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        final List<Long> matchedUserIds;
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .like(User::getUsername, queryDTO.getKeyword()));
            matchedUserIds = users.stream().map(User::getId).collect(Collectors.toList());
        } else {
            matchedUserIds = null;
        }
        
        final List<Long> authorUserIds;
        if (StrUtil.isNotBlank(queryDTO.getAuthorName())) {
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .like(User::getUsername, queryDTO.getAuthorName()));
            authorUserIds = users.stream().map(User::getId).collect(Collectors.toList());
            if (authorUserIds.isEmpty()) {
                queryWrapper.apply("1 = 0");
                return queryWrapper;
            }
        } else {
            authorUserIds = null;
        }
        
        queryWrapper.eq(queryDTO.getUserId() != null, ApiInfo::getUserId, queryDTO.getUserId())
                .and(StrUtil.isNotBlank(queryDTO.getKeyword()), wrapper -> {
                    wrapper.like(ApiInfo::getName, queryDTO.getKeyword())
                            .or()
                            .like(ApiInfo::getDescription, queryDTO.getKeyword());
                    if (matchedUserIds != null && !matchedUserIds.isEmpty()) {
                        wrapper.or().in(ApiInfo::getUserId, matchedUserIds);
                    }
                })
                .eq(queryDTO.getTypeId() != null, ApiInfo::getTypeId, queryDTO.getTypeId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), ApiInfo::getStatus, queryDTO.getStatus())
                .in(authorUserIds != null && !authorUserIds.isEmpty(), ApiInfo::getUserId, authorUserIds);
        
        applySorting(queryWrapper, queryDTO);
        return queryWrapper;
    }

    private void applySorting(LambdaQueryWrapper<ApiInfo> queryWrapper, ApiQueryDTO queryDTO) {
        if (StrUtil.isNotBlank(queryDTO.getSortBy())) {
            String sortBy = queryDTO.getSortBy();
            String sortOrder = queryDTO.getSortOrder();
            boolean isAsc;
            
            switch (sortBy) {
                case "price":
                    isAsc = StrUtil.isBlank(sortOrder) || "asc".equalsIgnoreCase(sortOrder);
                    queryWrapper.orderBy(true, isAsc, ApiInfo::getPrice);
                    break;
                case "rating":
                    isAsc = StrUtil.isBlank(sortOrder) || "asc".equalsIgnoreCase(sortOrder);
                    queryWrapper.orderBy(true, isAsc, ApiInfo::getRating);
                    break;
                case "invokeCount":
                    isAsc = !StrUtil.isBlank(sortOrder) && "asc".equalsIgnoreCase(sortOrder);
                    queryWrapper.orderBy(true, isAsc, ApiInfo::getInvokeCount);
                    break;
                default:
                    queryWrapper.orderByDesc(ApiInfo::getCreateTime);
            }
        } else {
            queryWrapper.orderByDesc(ApiInfo::getCreateTime);
        }
    }

    private Map<Long, String> getTypeNameMap(List<ApiInfo> apiInfos) {
        List<Long> typeIds = apiInfos.stream()
                .map(ApiInfo::getTypeId)
                .distinct()
                .collect(Collectors.toList());
        if (typeIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return apiTypeMapper.selectBatchIds(typeIds).stream()
                .collect(Collectors.toMap(ApiType::getId, ApiType::getName));
    }

    private Map<Long, String> getUsernameMap(List<ApiInfo> apiInfos) {
        List<Long> userIds = apiInfos.stream()
                .map(ApiInfo::getUserId)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    private void copyCreateDtoToEntity(ApiCreateDTO dto, ApiInfo entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setTypeId(dto.getTypeId());
        entity.setEndpoint(dto.getEndpoint());
        entity.setTargetUrl(dto.getTargetUrl());
        entity.setMethod(dto.getMethod());
        entity.setPrice(dto.getPrice());
        entity.setPriceUnit(dto.getPriceUnit());
        entity.setCallLimit(dto.getCallLimit());
        entity.setDocUrl(dto.getDocUrl());
        entity.setRequestParams(dto.getRequestParamsJson());
        entity.setResponseParams(dto.getResponseParamsJson());
    }

    private void validateStatusTransition(String currentStatus, String newStatus, boolean isUserOperation) {
        if ("approved".equals(currentStatus)) {
            if (!"offline".equals(newStatus)) {
                throw new BusinessException("已通过API只能下架");
            }
        } else if ("offline".equals(currentStatus)) {
            if (!"pending".equals(newStatus)) {
                throw new BusinessException("已下架API只能重新提交审核");
            }
        } else if ("rejected".equals(currentStatus)) {
            if (!"pending".equals(newStatus)) {
                throw new BusinessException("已拒绝API只能重新提交审核");
            }
        } else if ("pending".equals(currentStatus)) {
            if (!"offline".equals(newStatus)) {
                throw new BusinessException("待审核中的API只能下架");
            }
        } else {
            throw new BusinessException("当前状态不允许此操作");
        }
    }

    private void validateAuditStatusTransition(String currentStatus, String newStatus) {
        if ("pending".equals(currentStatus)) {
            if (!"approved".equals(newStatus) && !"rejected".equals(newStatus)) {
                throw new BusinessException("待审核API只能变更为已通过或已拒绝");
            }
        } else if ("approved".equals(currentStatus)) {
            if (!"offline".equals(newStatus)) {
                throw new BusinessException("已通过API只能变更为已下架");
            }
        } else if ("offline".equals(currentStatus)) {
            if (!"approved".equals(newStatus)) {
                throw new BusinessException("已下架API只能变更为已通过");
            }
        } else {
            throw new BusinessException("当前状态不允许变更");
        }
    }

}
