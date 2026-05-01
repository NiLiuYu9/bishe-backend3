package com.api.platform.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.api.platform.client.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * API客户端SDK —— 封装API调用的HTTP请求和签名逻辑
 *
 * 使用方式：
 * <pre>
 *   ApiClient client = new ApiClient("your-accessKey", "your-secretKey", "http://gateway-host:8090");
 *   String result = client.get("/invoke/api_1", null);
 * </pre>
 *
 * 自动处理：
 * - 请求签名（SHA256）
 * - 签名头注入（accessKey、nonce、timestamp、sign）
 * - 响应解析
 */
public class ApiClient {

    private final String accessKey;

    private final String secretKey;

    private final String gatewayHost;

    public ApiClient(String accessKey, String secretKey, String gatewayHost) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.gatewayHost = gatewayHost;
    }

    public String request(String method, String path, Map<String, Object> params) {
        String body = params == null ? "" : JSONUtil.toJsonStr(params);
        String url = gatewayHost + path;

        HttpRequest httpRequest;
        if ("GET".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.get(url);
        } else if ("POST".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.post(url).body(body);
        } else if ("PUT".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.put(url).body(body);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.delete(url);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        Map<String, String> headers = getHeaders(body);
        headers.forEach(httpRequest::header);

        HttpResponse response = httpRequest.execute();
        return response.body();
    }

    public String get(String path, Map<String, Object> params) {
        return request("GET", path, params);
    }

    public String post(String path, Map<String, Object> params) {
        return request("POST", path, params);
    }

    public String put(String path, Map<String, Object> params) {
        return request("PUT", path, params);
    }

    public String delete(String path, Map<String, Object> params) {
        return request("DELETE", path, params);
    }

    private Map<String, String> getHeaders(String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accessKey", accessKey);
        headers.put("nonce", RandomUtil.randomNumbers(4));
        headers.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        headers.put("body", body);
        headers.put("sign", SignUtils.genSign(body, secretKey));
        return headers;
    }
}
