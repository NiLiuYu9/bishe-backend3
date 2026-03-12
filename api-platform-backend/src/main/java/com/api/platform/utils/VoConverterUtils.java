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

public final class VoConverterUtils {

    private VoConverterUtils() {
    }

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

    public static UserVO convertToUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    public static List<ApiVO> convertToApiVOList(List<ApiInfo> apiInfos, Map<Long, String> typeNameMap, Map<Long, String> usernameMap) {
        if (apiInfos == null || apiInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return apiInfos.stream()
                .map(apiInfo -> convertToApiVO(apiInfo, typeNameMap, usernameMap))
                .collect(Collectors.toList());
    }

    public static List<UserVO> convertToUserVOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(VoConverterUtils::convertToUserVO)
                .collect(Collectors.toList());
    }
}
