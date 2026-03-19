package com.api.platform.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.json.JSONUtil;
import com.api.platform.common.Result;
import com.api.platform.config.GatewayConfig;
import com.api.platform.dto.TestRecordDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiTestRecord;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.service.AccessKeyService;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.ApiTestRecordService;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.utils.VoConverterUtils;
import com.api.platform.vo.ApiInvokeResultVO;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.TestRecordVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private static final int MAX_RECORDS_PER_USER_API = 5;
    private static final int MAX_DAILY_CALLS_PER_USER_API = 5;

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private ApiTestRecordService apiTestRecordService;

    @Autowired
    private ApiCacheService apiCacheService;

    @PostMapping("/call")
    public Result<ApiInvokeResultVO> testCall(@RequestBody TestCallDTO dto, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);

        User user = accessKeyService.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (user.getAccessKey() == null || user.getAccessKey().isEmpty()) {
            accessKeyService.generateAccessKey(userId);
            user = accessKeyService.getById(userId);
        }

        ApiVO apiVO = apiCacheService.getApiDetailFromCache(dto.getApiId());
        if (apiVO == null) {
            if (apiCacheService.isNullValueCached(dto.getApiId())) {
                throw new BusinessException(404, "API不存在");
            }
            ApiInfo apiInfo = apiInfoMapper.selectById(dto.getApiId());
            if (apiInfo == null) {
                apiCacheService.cacheNullValue(dto.getApiId());
                throw new BusinessException(404, "API不存在");
            }
            apiVO = VoConverterUtils.convertToApiVO(apiInfo);
            apiCacheService.cacheApiDetail(dto.getApiId(), apiVO);
        }
        if (!"approved".equals(apiVO.getStatus())) {
            throw new BusinessException(403, "API未审核通过或已下架");
        }

        int todayCallCount = apiTestRecordService.countTodayCallsByUserIdAndApiId(userId, dto.getApiId());
        if (todayCallCount >= MAX_DAILY_CALLS_PER_USER_API) {
            throw new BusinessException(403, "今日该API测试调用次数已达上限(" + MAX_DAILY_CALLS_PER_USER_API + "次)，请明天再试");
        }

        userApiQuotaService.deductQuota(userId, dto.getApiId());

        ApiInvokeResultVO vo = new ApiInvokeResultVO();
        vo.setApiName(apiVO.getName());
        vo.setEndpoint(apiVO.getEndpoint());
        vo.setMethod(apiVO.getMethod());
        vo.setRequestParams(dto.getParams());

        long startTime = System.currentTimeMillis();
        try {
            Object result = callTargetApi(apiVO, user.getAccessKey(), user.getSecretKey(), dto.getParams());
            log.info("API调用结果: {}", result);
            vo.setSuccess(true);
            vo.setMessage("API调用成功");
            vo.setResult(result);
            vo.setStatusCode(200);
        } catch (Exception e) {
            log.error("API调用失败", e);
            vo.setSuccess(false);
            vo.setMessage("API调用失败: " + e.getMessage());
            vo.setStatusCode(500);
        }
        long endTime = System.currentTimeMillis();
        vo.setResponseTime(endTime - startTime);

        String paramsJson = dto.getParams() != null ? JSONUtil.toJsonStr(dto.getParams()) : null;
        String resultJson = vo.getResult() != null ? JSONUtil.toJsonStr(vo.getResult()) : null;
        apiTestRecordService.saveAutoCallRecord(
            userId,
            dto.getApiId(),
            apiVO.getName(),
            paramsJson,
            resultJson,
            vo.getSuccess(),
            vo.getSuccess() ? null : vo.getMessage(),
            vo.getResponseTime().intValue(),
            vo.getStatusCode()
        );

        log.info("返回结果: {}", JSONUtil.toJsonStr(vo));
        return Result.success(vo);
    }

    private Object callTargetApi(ApiVO apiVO, String accessKey, String secretKey, Map<String, Object> params) {
        String targetUrl = apiVO.getTargetUrl();
        String endpoint = apiVO.getEndpoint();
        
        String url;
        if (StrUtil.isNotBlank(targetUrl)) {
            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                targetUrl = "http://" + targetUrl;
            }
            url = targetUrl + endpoint;
        } else {
            url = gatewayConfig.getGatewayUrl() + endpoint;
        }
        
        String body = params != null ? JSONUtil.toJsonStr(params) : "";
        
        String nonce = String.valueOf(IdUtil.getSnowflakeNextId() % 10000);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = genSign(body, secretKey);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("accessKey", accessKey);
        headers.set("nonce", nonce);
        headers.set("timestamp", timestamp);
        headers.set("sign", sign);
        headers.set("body", body);
        
        try {
            if (params != null && !params.isEmpty()) {
                StringBuilder urlBuilder = new StringBuilder(url);
                urlBuilder.append("?");
                params.forEach((key, value) -> {
                    if (value != null) {
                        urlBuilder.append(key).append("=").append(value).append("&");
                    }
                });
                url = urlBuilder.substring(0, urlBuilder.length() - 1);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            log.info("请求URL: {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            log.info("响应状态: {}, 响应体: {}", response.getStatusCode(), response.getBody());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> resultMap = objectMapper.readValue(response.getBody(), Map.class);
                Object data = resultMap.get("data");
                log.info("解析后的data字段: {}", data);
                return data;
            }
        } catch (Exception e) {
            throw new BusinessException(500, "调用API失败: " + e.getMessage());
        }

        return null;
    }

    private String genSign(String body, String secretKey) {
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return digester.digestHex(content);
    }

    @Data
    public static class TestCallDTO {
        private Long apiId;
        private Map<String, Object> params;
    }

    @GetMapping("/records/count")
    public Result<Integer> getRecordCount(@RequestParam Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        int count = apiTestRecordService.countByUserIdAndApiId(userId, apiId);
        return Result.success(count);
    }

    @GetMapping("/daily-calls/remaining")
    public Result<Integer> getRemainingDailyCalls(@RequestParam Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        int todayCallCount = apiTestRecordService.countTodayCallsByUserIdAndApiId(userId, apiId);
        int remaining = Math.max(0, MAX_DAILY_CALLS_PER_USER_API - todayCallCount);
        return Result.success(remaining);
    }

    @PostMapping("/save-record")
    public Result<TestRecordVO> saveRecord(@RequestBody TestRecordDTO dto, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        
        int count = apiTestRecordService.countByUserIdAndApiId(userId, dto.getApiId());
        if (count >= MAX_RECORDS_PER_USER_API) {
            throw new BusinessException(400, "测试记录已达上限(" + MAX_RECORDS_PER_USER_API + "条)，请删除部分记录后再保存");
        }

        String paramsJson = dto.getParams() != null ? JSONUtil.toJsonStr(dto.getParams()) : null;
        String resultJson = dto.getResult() != null ? JSONUtil.toJsonStr(dto.getResult()) : null;

        apiTestRecordService.saveRecord(
            userId,
            dto.getApiId(),
            dto.getApiName(),
            paramsJson,
            resultJson,
            dto.getSuccess() != null && dto.getSuccess(),
            dto.getErrorMsg(),
            dto.getResponseTime(),
            dto.getStatusCode()
        );

        List<ApiTestRecord> records = apiTestRecordService.getRecordsByUserIdAndApiId(userId, dto.getApiId());
        ApiTestRecord savedRecord = records.stream()
            .filter(r -> r.getParams() != null && r.getParams().equals(paramsJson))
            .findFirst()
            .orElse(null);

        if (savedRecord != null) {
            TestRecordVO vo = convertToVO(savedRecord);
            return Result.success(vo);
        }
        return Result.success(null);
    }

    @GetMapping("/records")
    public Result<List<TestRecordVO>> getRecords(@RequestParam Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        List<ApiTestRecord> records = apiTestRecordService.getRecordsByUserIdAndApiId(userId, apiId);
        List<TestRecordVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        return Result.success(voList);
    }

    @DeleteMapping("/records/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiTestRecordService.deleteRecord(userId, id);
        return Result.success();
    }

    private TestRecordVO convertToVO(ApiTestRecord record) {
        TestRecordVO vo = new TestRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setSuccess(record.getSuccess() == 1);
        try {
            if (record.getParams() != null) {
                vo.setParams(objectMapper.readValue(record.getParams(), Map.class));
            }
            if (record.getResult() != null) {
                vo.setResult(objectMapper.readValue(record.getResult(), Object.class));
            }
        } catch (Exception e) {
            log.error("解析JSON失败", e);
        }
        return vo;
    }
}
