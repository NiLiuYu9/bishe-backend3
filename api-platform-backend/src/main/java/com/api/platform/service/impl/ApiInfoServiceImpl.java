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

/**
 * API信息服务实现 —— 处理API的CRUD、缓存管理、审核流程、状态流转等核心业务逻辑
 *
 * API状态流转规则：
 *   用户操作：pending(待审核) → offline(已下架)
 *            approved(已通过) → offline(已下架)
 *            offline(已下架) → pending(重新提交审核)
 *            rejected(已拒绝) → pending(重新提交审核)
 *   管理员审核：pending → approved(通过) / rejected(拒绝)
 *              approved → offline(下架)
 *              offline → approved(重新上架)
 *
 * 缓存策略：
 * - API详情缓存到Redis Hash（key: api:info:{id}）
 * - 路径映射缓存（endpoint+method → apiId），用于网关快速路由
 * - 空值缓存防止缓存穿透（key: api:null:{id}）
 * - 列表缓存：API变更时清除所有列表缓存
 */
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

    /** 分页查询API列表（不携带当前用户收藏信息） */
    @Override
    public IPage<ApiVO> getApis(ApiQueryDTO queryDTO) {
        return getApis(queryDTO, null);
    }

    /**
     * 分页查询API列表（携带当前用户收藏信息）
     *
     * 业务流程：
     * 1. 构建查询条件（支持关键词、分类、状态、作者名筛选）
     * 2. 执行分页查询
     * 3. 批量获取分类名称和用户名（避免N+1查询）
     * 4. 查询当前用户的收藏列表，标记isFavorited
     * 5. 转换为ApiVO返回
     */
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

    /** 获取API详情（不携带当前用户收藏信息） */
    @Override
    public ApiVO getApiDetailById(Long id) {
        return getApiDetailById(id, null);
    }

    /**
     * 获取API详情（携带当前用户收藏信息）
     *
     * 缓存策略：
     * 1. 先检查空值缓存（防止缓存穿透）
     * 2. 命中缓存则直接返回
     * 3. 未命中则查数据库，写入缓存
     * 4. 同时缓存路径映射（endpoint+method → apiId）
     */
    @Override
    public ApiVO getApiDetailById(Long id, Long currentUserId) {
        if (apiCacheService.isNullValueCached(id)) { // 空值缓存防穿透
            return null;
        }

        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(id); // 尝试从Redis获取
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
            apiCacheService.cacheNullValue(id); // 缓存空值，防止缓存穿透
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

        apiCacheService.cacheApiDetail(id, apiVO); // 写入Redis缓存
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id); // 缓存路径映射

        return apiVO;
    }

    /**
     * 创建API
     *
     * 业务流程：
     * 1. DTO转Entity，设置初始状态为pending（待审核）
     * 2. 初始化统计字段（rating=0, invokeCount=0等）
     * 3. 保存到数据库
     * 4. 写入Redis缓存（详情缓存 + 路径映射缓存）
     * 5. 清除列表缓存
     */
    @Override
    public ApiVO createApi(Long userId, ApiCreateDTO createDTO) {
        ApiInfo apiInfo = new ApiInfo();
        copyCreateDtoToEntity(createDTO, apiInfo);
        apiInfo.setUserId(userId);
        apiInfo.setStatus("pending"); // 初始状态：待审核
        apiInfo.setRating(BigDecimal.ZERO); // 初始评分：0
        apiInfo.setInvokeCount(0L); // 初始调用次数：0
        apiInfo.setSuccessCount(0L);
        apiInfo.setFailCount(0L);
        save(apiInfo);
        
        ApiType apiType = apiTypeMapper.selectById(apiInfo.getTypeId());
        User user = userMapper.selectById(userId);
        ApiVO apiVO = VoConverterUtils.convertToApiVO(apiInfo, apiType, user);
        
        apiCacheService.cacheApiDetail(apiInfo.getId(), apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiInfo.getId());
        apiCacheService.clearListCache(); // 新增API后清除列表缓存
        
        return apiVO;
    }

    /**
     * 更新API
     *
     * 业务流程：
     * 1. 校验API存在性和操作权限
     * 2. 更新API信息，状态重置为pending（需重新审核）
     * 3. 更新Redis缓存（详情缓存 + 路径映射缓存）
     * 4. 如调用限额变更，清除限流缓存
     * 5. 如endpoint/method变更，删除旧路径映射
     * 6. 清除列表缓存
     */
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
        oldApiInfo.setStatus("pending"); // 更新后需重新审核
        updateById(oldApiInfo);
        
        ApiType apiType = apiTypeMapper.selectById(oldApiInfo.getTypeId());
        User user = userMapper.selectById(userId);
        ApiVO apiVO = VoConverterUtils.convertToApiVO(oldApiInfo, apiType, user);
        
        apiCacheService.cacheApiDetail(apiId, apiVO);
        
        Integer newCallLimit = oldApiInfo.getCallLimit();
        // 调用限额变更时，清除限流缓存
        if (oldCallLimit == null && newCallLimit != null || 
            oldCallLimit != null && !oldCallLimit.equals(newCallLimit)) {
            apiCacheService.clearRateLimitCache(apiId);
        }
        
        // endpoint或method变更时，删除旧路径映射
        if (!Objects.equals(oldEndpoint, oldApiInfo.getEndpoint()) || !Objects.equals(oldMethod, oldApiInfo.getMethod())) {
            apiCacheService.deletePathMapping(oldEndpoint, oldMethod);
        }
        apiCacheService.cachePathMapping(oldApiInfo.getEndpoint(), oldApiInfo.getMethod(), apiId);
        apiCacheService.clearListCache();
        
        return apiVO;
    }

    /**
     * 用户更新API状态（下架/重新提交审核）
     * 校验状态流转合法性后更新，并同步刷新缓存
     */
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
        
        validateStatusTransition(currentStatus, newStatus, true); // 校验用户操作的状态流转合法性
        
        LambdaUpdateWrapper<ApiInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApiInfo::getId, apiId)
                .set(ApiInfo::getStatus, newStatus);
        update(updateWrapper);

        apiInfo.setStatus(newStatus);
        ApiVO apiVO = getApiVOFromEntity(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.clearListCache();
    }

    /**
     * 管理员审核API
     * 校验审核状态流转合法性后更新，并同步刷新缓存
     */
    @Override
    public void auditApi(Long apiId, AuditApiDTO auditApiDTO) {
        ApiInfo apiInfo = getById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        String currentStatus = apiInfo.getStatus();
        String newStatus = auditApiDTO.getStatus();
        
        validateAuditStatusTransition(currentStatus, newStatus); // 校验管理员审核的状态流转合法性
        
        LambdaUpdateWrapper<ApiInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApiInfo::getId, apiId)
                .set(ApiInfo::getStatus, newStatus);
        update(updateWrapper);

        apiInfo.setStatus(newStatus);
        ApiVO apiVO = getApiVOFromEntity(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.clearListCache();
    }

    /** Entity转ApiVO（不含typeName和username，用于缓存更新场景） */
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

    /** 构建API查询条件（支持关键词、分类、状态、作者名筛选及多种排序方式） */
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

    /** 应用排序规则（支持按价格、评分、调用次数排序，默认按创建时间倒序） */
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

    /** 批量获取分类名称映射（typeId → typeName） */
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

    /** 批量获取用户名映射（userId → username） */
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

    /** DTO字段拷贝到Entity */
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

    /**
     * 校验用户操作的状态流转合法性
     * approved → offline, offline → pending, rejected → pending, pending → offline
     */
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

    /**
     * 校验管理员审核的状态流转合法性
     * pending → approved/rejected, approved → offline, offline → approved
     */
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
