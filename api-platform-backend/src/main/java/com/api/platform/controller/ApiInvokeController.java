package com.api.platform.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.platform.common.Result;
import com.api.platform.config.GatewayConfig;
import com.api.platform.dto.ApiInvokeDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.User;
import com.api.platform.entity.UserApiQuota;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.service.AccessKeyService;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.vo.ApiInvokeResultVO;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.QuotaCheckVO;
import com.api.platform.vo.UserQuotaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoke")
public class ApiInvokeController {

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private ApiCacheService apiCacheService;

    @PostMapping("/call")
    public Result<ApiInvokeResultVO> invokeApi(@RequestBody ApiInvokeDTO invokeDTO) {
        User user = accessKeyService.validateAccessKey(invokeDTO.getAccessKey(), invokeDTO.getSecretKey());
        
        ApiInfo apiInfo = getApiInfoWithCache(invokeDTO.getApiId());
        if (apiInfo == null) {
            throw new BusinessException(404, "API不存在");
        }
        if (!"approved".equals(apiInfo.getStatus())) {
            throw new BusinessException(403, "API未审核通过或已下架");
        }
        userApiQuotaService.deductQuota(user.getId(), invokeDTO.getApiId());

        ApiInvokeResultVO vo = new ApiInvokeResultVO();
        vo.setApiName(apiInfo.getName());
        vo.setEndpoint(apiInfo.getEndpoint());
        vo.setMethod(apiInfo.getMethod());
        vo.setRequestParams(invokeDTO.getParams());

        try {
            Object result = callTargetApi(apiInfo, invokeDTO.getAccessKey(), invokeDTO.getSecretKey(), invokeDTO.getParams());
            vo.setSuccess(true);
            vo.setMessage("API调用成功");
            vo.setResult(result);
        } catch (Exception e) {
            vo.setSuccess(false);
            vo.setMessage("API调用失败: " + e.getMessage());
        }

        return Result.success(vo);
    }

    private ApiInfo getApiInfoWithCache(Long apiId) {
        if (apiCacheService.isNullValueCached(apiId)) {
            return null;
        }

        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(apiId);
        if (cachedVO != null) {
            return convertToApiInfo(cachedVO);
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(apiId);
            return null;
        }

        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiId);

        return apiInfo;
    }

    private Object callTargetApi(ApiInfo apiInfo, String accessKey, String secretKey, Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Access-Key", accessKey);
        headers.set("X-Secret-Key", secretKey);

        String targetUrl = apiInfo.getTargetUrl();
        String endpoint = apiInfo.getEndpoint();
        
        String baseUrl;
        if (StrUtil.isNotBlank(targetUrl)) {
            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                targetUrl = "http://" + targetUrl;
            }
            baseUrl = targetUrl + endpoint;
        } else {
            baseUrl = gatewayConfig.getGatewayUrl() + endpoint;
        }
        
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                params.forEach((key, value) -> {
                    urlBuilder.append(key).append("=").append(value).append("&");
                });
                baseUrl = urlBuilder.substring(0, urlBuilder.length() - 1);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject result = JSONUtil.parseObj(response.getBody());
                return result.get("data");
            }
        } catch (Exception e) {
            throw new BusinessException(500, "调用API失败: " + e.getMessage());
        }

        return null;
    }

    @GetMapping("/quota/list")
    public Result<List<UserQuotaVO>> getUserQuotas(
            @RequestParam String accessKey,
            @RequestParam String secretKey) {
        User user = accessKeyService.validateAccessKey(accessKey, secretKey);
        List<UserApiQuota> quotas = userApiQuotaService.getUserQuotas(user.getId());
        
        Map<Long, String> apiNameMap = new HashMap<>();
        for (UserApiQuota quota : quotas) {
            if (!apiNameMap.containsKey(quota.getApiId())) {
                String apiName = getApiNameWithCache(quota.getApiId());
                apiNameMap.put(quota.getApiId(), apiName);
            }
        }
        
        List<UserQuotaVO> voList = quotas.stream().map(quota -> {
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
        return Result.success(voList);
    }

    private String getApiNameWithCache(Long apiId) {
        ApiVO cachedVO = apiCacheService.getApiDetailFromCache(apiId);
        if (cachedVO != null) {
            return cachedVO.getName();
        }

        if (apiCacheService.isNullValueCached(apiId)) {
            return null;
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(apiId);
            return null;
        }

        ApiVO apiVO = convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiId);

        return apiInfo.getName();
    }

    @GetMapping("/quota/check")
    public Result<QuotaCheckVO> checkQuota(
            @RequestParam String accessKey,
            @RequestParam String secretKey,
            @RequestParam Long apiId) {
        User user = accessKeyService.validateAccessKey(accessKey, secretKey);
        UserApiQuota quota = userApiQuotaService.getQuota(user.getId(), apiId);
        QuotaCheckVO vo = new QuotaCheckVO();
        vo.setUserId(user.getId());
        vo.setApiId(apiId);
        if (quota != null) {
            vo.setHasQuota(true);
            vo.setTotalCount(quota.getTotalCount());
            vo.setUsedCount(quota.getUsedCount());
            vo.setRemainingCount(quota.getRemainingCount());
        } else {
            vo.setHasQuota(false);
            vo.setTotalCount(0);
            vo.setUsedCount(0);
            vo.setRemainingCount(0);
        }
        return Result.success(vo);
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

    private ApiInfo convertToApiInfo(ApiVO vo) {
        if (vo == null) {
            return null;
        }
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setId(vo.getId());
        apiInfo.setName(vo.getName());
        apiInfo.setDescription(vo.getDescription());
        apiInfo.setTypeId(vo.getTypeId());
        apiInfo.setUserId(vo.getUserId());
        apiInfo.setMethod(vo.getMethod());
        apiInfo.setEndpoint(vo.getEndpoint());
        apiInfo.setTargetUrl(vo.getTargetUrl());
        apiInfo.setPrice(vo.getPrice());
        apiInfo.setPriceUnit(vo.getPriceUnit());
        apiInfo.setCallLimit(vo.getCallLimit());
        apiInfo.setStatus(vo.getStatus());
        apiInfo.setCreateTime(vo.getCreateTime());
        apiInfo.setUpdateTime(vo.getUpdateTime());
        apiInfo.setDocUrl(vo.getDocUrl());
        apiInfo.setRating(vo.getRating());
        apiInfo.setInvokeCount(vo.getInvokeCount());
        apiInfo.setSuccessCount(vo.getSuccessCount());
        apiInfo.setFailCount(vo.getFailCount());
        return apiInfo;
    }

}
