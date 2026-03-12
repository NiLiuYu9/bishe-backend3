package com.api.platform.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.json.JSONUtil;
import com.api.platform.common.Result;
import com.api.platform.config.GatewayConfig;
import com.api.platform.constants.SessionConstants;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.service.AccessKeyService;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.vo.ApiInvokeResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

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

    @PostMapping("/call")
    public Result<ApiInvokeResultVO> testCall(@RequestBody TestCallDTO dto, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        User user = accessKeyService.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (user.getAccessKey() == null || user.getAccessKey().isEmpty()) {
            accessKeyService.generateAccessKey(userId);
            user = accessKeyService.getById(userId);
        }

        ApiInfo apiInfo = apiInfoMapper.selectById(dto.getApiId());
        if (apiInfo == null) {
            throw new BusinessException(404, "API不存在");
        }
        if (!"approved".equals(apiInfo.getStatus())) {
            throw new BusinessException(403, "API未审核通过或已下架");
        }

        userApiQuotaService.deductQuota(userId, dto.getApiId());

        ApiInvokeResultVO vo = new ApiInvokeResultVO();
        vo.setApiName(apiInfo.getName());
        vo.setEndpoint(apiInfo.getEndpoint());
        vo.setMethod(apiInfo.getMethod());
        vo.setRequestParams(dto.getParams());

        try {
            Object result = callMockApi(apiInfo.getEndpoint(), user.getAccessKey(), user.getSecretKey(), dto.getParams());
            log.info("API调用结果: {}", result);
            vo.setSuccess(true);
            vo.setMessage("API调用成功");
            vo.setResult(result);
        } catch (Exception e) {
            log.error("API调用失败", e);
            vo.setSuccess(false);
            vo.setMessage("API调用失败: " + e.getMessage());
        }

        log.info("返回结果: {}", JSONUtil.toJsonStr(vo));
        return Result.success(vo);
    }

    private Object callMockApi(String endpoint, String accessKey, String secretKey, Map<String, Object> params) {
        String url = gatewayConfig.getApiUrl(endpoint);
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
}
