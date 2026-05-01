package com.api.platform.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.api.platform.dto.ApiParamDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiType;
import com.api.platform.entity.User;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.UserVO;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Entity→VO转换工具类
 * <p>核心职责：统一封装Entity到VO的转换逻辑，避免在Controller和Service中重复编写转换代码。
 * 支持ApiInfo→ApiVO、User→UserVO的转换，以及批量转换和反向转换。</p>
 */
public final class VoConverterUtils {

    private VoConverterUtils() {
    }

    /**
     * ApiInfo → ApiVO简单转换（不含分类名称和用户名）
     *
     * @param apiInfo API信息实体
     * @return API详情VO
     */
    public static ApiVO convertToApiVO(ApiInfo apiInfo) {
        return convertToApiVO(apiInfo, (Map<Long, String>) null, (Map<Long, String>) null);
    }

    /**
     * ApiInfo → ApiVO转换（含分类名称和用户名映射）
     * <p>使用BeanUtils复制基础属性，额外填充typeName、username，
     * 并将请求参数和响应参数的JSON字符串解析为对象列表。</p>
     *
     * @param apiInfo      API信息实体
     * @param typeNameMap  分类ID→名称映射（可为null）
     * @param usernameMap  用户ID→用户名映射（可为null）
     * @return API详情VO，apiInfo为null时返回null
     */
    public static ApiVO convertToApiVO(ApiInfo apiInfo, Map<Long, String> typeNameMap, Map<Long, String> usernameMap) {
        if (apiInfo == null) {
            return null;
        }
        ApiVO apiVO = new ApiVO();
        BeanUtils.copyProperties(apiInfo, apiVO);
        apiVO.setTypeId(apiInfo.getTypeId());
        if (typeNameMap != null) {
            apiVO.setTypeName(typeNameMap.get(apiInfo.getTypeId()));
        }
        if (usernameMap != null) {
            apiVO.setUsername(usernameMap.get(apiInfo.getUserId()));
        }
        if (StrUtil.isNotBlank(apiInfo.getRequestParams())) {
            apiVO.setRequestParams(JSONUtil.toList(apiInfo.getRequestParams(), ApiParamDTO.class));
        }
        if (StrUtil.isNotBlank(apiInfo.getResponseParams())) {
            apiVO.setResponseParams(JSONUtil.toList(apiInfo.getResponseParams(), ApiParamDTO.class));
        }
        return apiVO;
    }

    /**
     * ApiInfo → ApiVO转换（含收藏状态）
     * <p>在基础转换之上，额外设置isFavorited字段。</p>
     *
     * @param apiInfo         API信息实体
     * @param typeNameMap     分类ID→名称映射
     * @param usernameMap     用户ID→用户名映射
     * @param favoritedApiIds 用户已收藏的API ID集合
     * @return API详情VO
     */
    public static ApiVO convertToApiVO(ApiInfo apiInfo, Map<Long, String> typeNameMap, Map<Long, String> usernameMap, java.util.Set<Long> favoritedApiIds) {
        ApiVO apiVO = convertToApiVO(apiInfo, typeNameMap, usernameMap);
        if (apiVO != null && favoritedApiIds != null) {
            apiVO.setIsFavorited(favoritedApiIds.contains(apiInfo.getId()));
        }
        return apiVO;
    }

    /**
     * ApiInfo → ApiVO转换（直接传入分类和用户对象）
     * <p>适用于单条查询场景，直接传入ApiType和User对象获取名称。</p>
     *
     * @param apiInfo API信息实体
     * @param apiType API分类对象（可为null）
     * @param user    用户对象（可为null）
     * @return API详情VO
     */
    public static ApiVO convertToApiVO(ApiInfo apiInfo, ApiType apiType, User user) {
        if (apiInfo == null) {
            return null;
        }
        ApiVO apiVO = new ApiVO();
        BeanUtils.copyProperties(apiInfo, apiVO);
        apiVO.setTypeId(apiInfo.getTypeId());
        apiVO.setTypeName(apiType != null ? apiType.getName() : "");
        apiVO.setUsername(user != null ? user.getUsername() : "");
        if (StrUtil.isNotBlank(apiInfo.getRequestParams())) {
            apiVO.setRequestParams(JSONUtil.toList(apiInfo.getRequestParams(), ApiParamDTO.class));
        }
        if (StrUtil.isNotBlank(apiInfo.getResponseParams())) {
            apiVO.setResponseParams(JSONUtil.toList(apiInfo.getResponseParams(), ApiParamDTO.class));
        }
        return apiVO;
    }

    /**
     * User → UserVO转换
     *
     * @param user 用户实体
     * @return 用户VO，user为null时返回null
     */
    public static UserVO convertToUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 批量转换ApiInfo列表 → ApiVO列表
     *
     * @param apiInfos    API信息实体列表
     * @param typeNameMap 分类ID→名称映射
     * @param usernameMap 用户ID→用户名映射
     * @return API详情VO列表，输入为空时返回空列表
     */
    public static List<ApiVO> convertToApiVOList(List<ApiInfo> apiInfos, Map<Long, String> typeNameMap, Map<Long, String> usernameMap) {
        if (apiInfos == null || apiInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return apiInfos.stream()
                .map(apiInfo -> convertToApiVO(apiInfo, typeNameMap, usernameMap))
                .collect(Collectors.toList());
    }

    /**
     * 批量转换ApiInfo列表 → ApiVO列表（含收藏状态）
     *
     * @param apiInfos        API信息实体列表
     * @param typeNameMap     分类ID→名称映射
     * @param usernameMap     用户ID→用户名映射
     * @param favoritedApiIds 用户已收藏的API ID集合
     * @return API详情VO列表，输入为空时返回空列表
     */
    public static List<ApiVO> convertToApiVOList(List<ApiInfo> apiInfos, Map<Long, String> typeNameMap, Map<Long, String> usernameMap, java.util.Set<Long> favoritedApiIds) {
        if (apiInfos == null || apiInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return apiInfos.stream()
                .map(apiInfo -> convertToApiVO(apiInfo, typeNameMap, usernameMap, favoritedApiIds))
                .collect(Collectors.toList());
    }

    /**
     * 批量转换User列表 → UserVO列表
     *
     * @param users 用户实体列表
     * @return 用户VO列表，输入为空时返回空列表
     */
    public static List<UserVO> convertToUserVOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(VoConverterUtils::convertToUserVO)
                .collect(Collectors.toList());
    }

    /**
     * ApiVO → ApiInfo反向转换
     * <p>用于缓存反序列化等场景，将VO转回Entity。
     * 注意：不复制requestParams/responseParams的JSON字符串。</p>
     *
     * @param apiVO API详情VO
     * @return API信息实体，apiVO为null时返回null
     */
    public static ApiInfo convertToApiInfo(ApiVO apiVO) {
        if (apiVO == null) {
            return null;
        }
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setId(apiVO.getId());
        apiInfo.setName(apiVO.getName());
        apiInfo.setDescription(apiVO.getDescription());
        apiInfo.setTypeId(apiVO.getTypeId());
        apiInfo.setUserId(apiVO.getUserId());
        apiInfo.setMethod(apiVO.getMethod());
        apiInfo.setEndpoint(apiVO.getEndpoint());
        apiInfo.setTargetUrl(apiVO.getTargetUrl());
        apiInfo.setPrice(apiVO.getPrice());
        apiInfo.setPriceUnit(apiVO.getPriceUnit());
        apiInfo.setCallLimit(apiVO.getCallLimit());
        apiInfo.setStatus(apiVO.getStatus());
        apiInfo.setCreateTime(apiVO.getCreateTime());
        apiInfo.setUpdateTime(apiVO.getUpdateTime());
        apiInfo.setDocUrl(apiVO.getDocUrl());
        apiInfo.setRating(apiVO.getRating());
        apiInfo.setInvokeCount(apiVO.getInvokeCount());
        apiInfo.setSuccessCount(apiVO.getSuccessCount());
        apiInfo.setFailCount(apiVO.getFailCount());
        return apiInfo;
    }
}
