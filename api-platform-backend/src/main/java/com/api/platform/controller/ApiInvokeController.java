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
import com.api.platform.ratelimit.RateLimiter;
import com.api.platform.service.AccessKeyService;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.ApiWhitelistService;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.utils.VoConverterUtils;
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

/**
 * API调用控制器 —— 处理通过AK/SK直接调用API及配额查询请求
 *
 * 路由前缀：/invoke
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 与网关调用不同，此控制器用于前端页面内直接发起API调用，
 * 走的是后端代理转发模式，而非网关路由模式
 */
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

    @Autowired
    private ApiWhitelistService apiWhitelistService;

    @Autowired
    private RateLimiter rateLimiter;

    private static final int RATE_LIMIT_CAPACITY = 2;

    private static final int RATE_LIMIT_REFILL_RATE = 2;

    /**
     * 调用API
     *
     * 业务流程：
     * 1. 校验 accessKey/secretKey 有效性
     * 2. 从缓存或数据库获取API信息，校验审核状态
     * 3. 若API启用了白名单，检查用户是否在白名单中
     * 4. 令牌桶限流检查（按用户+API维度）
     * 5. 扣减用户配额
     * 6. 通过 RestTemplate 代理转发到目标API
     *
     * @param invokeDTO 调用请求（apiId、accessKey、secretKey、请求参数）
     * @return Result&lt;ApiInvokeResultVO&gt; 调用结果（成功/失败、响应数据、耗时等）
     */
    @PostMapping("/call")
    public Result<ApiInvokeResultVO> invokeApi(@RequestBody ApiInvokeDTO invokeDTO) {
        if (invokeDTO.getApiId() == null) {
            throw new BusinessException(400, "API ID不能为空");
        }
        if (StrUtil.isBlank(invokeDTO.getAccessKey()) || StrUtil.isBlank(invokeDTO.getSecretKey())) {
            throw new BusinessException(400, "AccessKey和SecretKey不能为空");
        }
        
        User user = accessKeyService.validateAccessKey(invokeDTO.getAccessKey(), invokeDTO.getSecretKey());
        
        ApiInfo apiInfo = getApiInfoWithCache(invokeDTO.getApiId());
        if (apiInfo == null) {
            throw new BusinessException(404, "API不存在");
        }
        if (!"approved".equals(apiInfo.getStatus())) {
            throw new BusinessException(403, "API未审核通过或已下架");
        }
        if (apiInfo.getWhitelistEnabled() != null && apiInfo.getWhitelistEnabled() == 1) {
            if (!apiWhitelistService.isInWhitelist(apiInfo.getId(), user.getId())) {
                throw new BusinessException(403, "您不在该API的白名单中，无法调用");
            }
        }

        String rateLimitKey = "invoke:" + user.getId() + ":" + apiInfo.getId();
        
        if (!rateLimiter.tryAcquire(rateLimitKey, RATE_LIMIT_CAPACITY, RATE_LIMIT_REFILL_RATE)) {
            throw new BusinessException(429, "API调用频率超限(每秒最多2次)，请稍后再试");
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
            return VoConverterUtils.convertToApiInfo(cachedVO);
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            apiCacheService.cacheNullValue(apiId);
            return null;
        }

        ApiVO apiVO = VoConverterUtils.convertToApiVO(apiInfo);
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

    /**
     * 查询用户所有API的配额列表
     *
     * 通过 accessKey/secretKey 鉴权后，返回该用户所有已购买API的配额使用情况
     *
     * @param accessKey 用户访问密钥
     * @param secretKey 用户秘密密钥
     * @return Result&lt;List&lt;UserQuotaVO&gt;&gt; 配额列表（含API名称、总量、已用量、剩余量）
     */
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

        ApiVO apiVO = VoConverterUtils.convertToApiVO(apiInfo);
        apiCacheService.cacheApiDetail(apiId, apiVO);
        apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), apiId);

        return apiInfo.getName();
    }

    /**
     * 检查用户对指定API的配额情况
     *
     * 通过 accessKey/secretKey 鉴权后，返回该用户对指定API的配额详情
     *
     * @param accessKey 用户访问密钥
     * @param secretKey 用户秘密密钥
     * @param apiId     API ID
     * @return Result&lt;QuotaCheckVO&gt; 配额检查结果（是否有配额、总量、已用量、剩余量）
     */
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

}
