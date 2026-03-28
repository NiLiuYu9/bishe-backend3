package com.api.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.api.platform.constants.ApiCacheConstant;
import com.api.platform.dto.ApiParamDTO;
import com.api.platform.service.ApiCacheService;
import com.api.platform.vo.ApiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.api.platform.constants.ApiCacheConstant.*;

@Service
public class ApiCacheServiceImpl implements ApiCacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ApiVO getApiDetailFromCache(Long id) {
        String key = API_INFO_KEY + id;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return convertMapToApiVO(entries);
    }

    @Override
    public void cacheApiDetail(Long id, ApiVO apiVO) {
        String key = API_INFO_KEY + id;
        Map<String, String> map = convertApiVOToMap(apiVO);
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void deleteApiDetailCache(Long id) {
        stringRedisTemplate.delete(API_INFO_KEY + id);
    }

    @Override
    public void cacheNullValue(Long id) {
        String key = API_NULL_KEY + id;
        stringRedisTemplate.opsForValue().set(key, "1", API_NULL_EXPIRE, TimeUnit.SECONDS);
    }

    @Override
    public boolean isNullValueCached(Long id) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(API_NULL_KEY + id));
    }

    @Override
    public Long getApiIdByPath(String endpoint, String method) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        String idStr = stringRedisTemplate.opsForValue().get(key);
        return idStr != null ? Long.parseLong(idStr) : null;
    }

    @Override
    public void cachePathMapping(String endpoint, String method, Long apiId) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        stringRedisTemplate.opsForValue().set(key, apiId.toString());
    }

    @Override
    public void deletePathMapping(String endpoint, String method) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void updateApiStatistics(Long id, Long invokeCount, Long successCount, Long failCount, BigDecimal rating) {
        String key = API_INFO_KEY + id;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Map<String, String> updates = new HashMap<>();
            updates.put("invokeCount", invokeCount.toString());
            updates.put("successCount", successCount.toString());
            updates.put("failCount", failCount.toString());
            updates.put("rating", rating.toString());
            stringRedisTemplate.opsForHash().putAll(key, updates);
        }
    }

    @Override
    public void clearListCache() {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(API_LIST_KEY + "*")
                .count(1000)
                .build();
        try (Cursor<String> cursor = stringRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                stringRedisTemplate.delete(cursor.next());
            }
        }
    }

    @Override
    public void clearRateLimitCache(Long apiId) {
        ApiVO apiVO = getApiDetailFromCache(apiId);
        if (apiVO == null || apiVO.getEndpoint() == null) {
            return;
        }
        String endpoint = apiVO.getEndpoint();
        String pattern = RATE_LIMIT_KEY + "*:" + endpoint;
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();
        try (Cursor<String> cursor = stringRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                stringRedisTemplate.delete(cursor.next());
            }
        }
    }

    private ApiVO convertMapToApiVO(Map<Object, Object> map) {
        ApiVO vo = new ApiVO();
        vo.setId(parseLong(map.get("id")));
        vo.setName(parseString(map.get("name")));
        vo.setDescription(parseString(map.get("description")));
        vo.setTypeName(parseString(map.get("typeName")));
        vo.setTypeId(parseLong(map.get("typeId")));
        vo.setUserId(parseLong(map.get("userId")));
        vo.setUsername(parseString(map.get("username")));
        vo.setMethod(parseString(map.get("method")));
        vo.setEndpoint(parseString(map.get("endpoint")));
        vo.setTargetUrl(parseString(map.get("targetUrl")));
        vo.setRequestParams(parseParamList(map.get("requestParams")));
        vo.setResponseParams(parseParamList(map.get("responseParams")));
        vo.setPrice(parseBigDecimal(map.get("price")));
        vo.setPriceUnit(parseString(map.get("priceUnit")));
        vo.setCallLimit(parseInteger(map.get("callLimit")));
        vo.setStatus(parseString(map.get("status")));
        vo.setCreateTime(parseLocalDateTime(map.get("createTime")));
        vo.setUpdateTime(parseLocalDateTime(map.get("updateTime")));
        vo.setDocUrl(parseString(map.get("docUrl")));
        vo.setRating(parseBigDecimal(map.get("rating")));
        vo.setInvokeCount(parseLong(map.get("invokeCount")));
        vo.setSuccessCount(parseLong(map.get("successCount")));
        vo.setFailCount(parseLong(map.get("failCount")));
        vo.setIsFavorited(parseBoolean(map.get("isFavorited")));
        return vo;
    }

    private Map<String, String> convertApiVOToMap(ApiVO vo) {
        Map<String, String> map = new HashMap<>();
        putIfNotNull(map, "id", vo.getId());
        putIfNotNull(map, "name", vo.getName());
        putIfNotNull(map, "description", vo.getDescription());
        putIfNotNull(map, "typeName", vo.getTypeName());
        putIfNotNull(map, "typeId", vo.getTypeId());
        putIfNotNull(map, "userId", vo.getUserId());
        putIfNotNull(map, "username", vo.getUsername());
        putIfNotNull(map, "method", vo.getMethod());
        putIfNotNull(map, "endpoint", vo.getEndpoint());
        putIfNotNull(map, "targetUrl", vo.getTargetUrl());
        putIfNotNull(map, "requestParams", vo.getRequestParams());
        putIfNotNull(map, "responseParams", vo.getResponseParams());
        putIfNotNull(map, "price", vo.getPrice());
        putIfNotNull(map, "priceUnit", vo.getPriceUnit());
        putIfNotNull(map, "callLimit", vo.getCallLimit());
        putIfNotNull(map, "status", vo.getStatus());
        putIfNotNull(map, "createTime", vo.getCreateTime());
        putIfNotNull(map, "updateTime", vo.getUpdateTime());
        putIfNotNull(map, "docUrl", vo.getDocUrl());
        putIfNotNull(map, "rating", vo.getRating());
        putIfNotNull(map, "invokeCount", vo.getInvokeCount());
        putIfNotNull(map, "successCount", vo.getSuccessCount());
        putIfNotNull(map, "failCount", vo.getFailCount());
        putIfNotNull(map, "isFavorited", vo.getIsFavorited());
        return map;
    }

    private void putIfNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            if (value instanceof java.time.LocalDateTime) {
                map.put(key, value.toString());
            } else if (value instanceof java.util.List) {
                map.put(key, JSONUtil.toJsonStr(value));
            } else if (value instanceof Boolean) {
                map.put(key, value.toString());
            } else {
                map.put(key, value.toString());
            }
        }
    }

    private String parseString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private Long parseLong(Object obj) {
        if (obj == null) return null;
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(Object obj) {
        if (obj == null) return null;
        try {
            return new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(Object obj) {
        if (obj == null) return null;
        return Boolean.parseBoolean(obj.toString());
    }

    private java.time.LocalDateTime parseLocalDateTime(Object obj) {
        if (obj == null) return null;
        try {
            return java.time.LocalDateTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private java.util.List<ApiParamDTO> parseParamList(Object obj) {
        if (obj == null) return null;
        try {
            return JSONUtil.toList(obj.toString(), ApiParamDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}
