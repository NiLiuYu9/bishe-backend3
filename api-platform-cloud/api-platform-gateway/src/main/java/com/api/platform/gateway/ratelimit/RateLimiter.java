package com.api.platform.gateway.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RateLimiter {

    private static final String RATE_LIMIT_KEY_PREFIX = "gateway_rate_limit:";

    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local capacity = tonumber(ARGV[1])\n" +
            "local refillRate = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local requested = tonumber(ARGV[4])\n" +
            "\n" +
            "local info = redis.call('hmget', key, 'tokens', 'lastRefillTime')\n" +
            "local tokens = tonumber(info[1])\n" +
            "local lastRefillTime = tonumber(info[2])\n" +
            "\n" +
            "if tokens == nil then\n" +
            "    tokens = capacity\n" +
            "    lastRefillTime = now\n" +
            "end\n" +
            "\n" +
            "local elapsed = now - lastRefillTime\n" +
            "if elapsed > 0 then\n" +
            "    local newTokens = elapsed * refillRate / 1000.0\n" +
            "    tokens = math.min(capacity, tokens + newTokens)\n" +
            "    lastRefillTime = now\n" +
            "end\n" +
            "\n" +
            "local allowed = 0\n" +
            "if tokens >= requested then\n" +
            "    tokens = tokens - requested\n" +
            "    allowed = 1\n" +
            "end\n" +
            "\n" +
            "redis.call('hmset', key, 'tokens', tokens, 'lastRefillTime', lastRefillTime)\n" +
            "redis.call('expire', key, 3600)\n" +
            "\n" +
            "return {allowed, math.floor(tokens)}";

    @SuppressWarnings("unchecked")
    private static final DefaultRedisScript<List> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>(LUA_SCRIPT, List.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean tryAcquire(String key, int capacity, int refillRate) {
        String fullKey = RATE_LIMIT_KEY_PREFIX + key;
        long now = System.currentTimeMillis();

        List<Long> result = stringRedisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                Collections.singletonList(fullKey),
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(now),
                "1"
        );

        if (result != null && !result.isEmpty()) {
            Long allowed = result.get(0);
            boolean acquired = allowed != null && allowed == 1;
            if (!acquired) {
                log.warn("Rate limit triggered: key={}, remaining={}", fullKey, result.size() > 1 ? result.get(1) : "N/A");
            } else {
                log.debug("Rate limit passed: key={}, remaining={}", fullKey, result.size() > 1 ? result.get(1) : "N/A");
            }
            return acquired;
        }
        log.error("Rate limit script returned null/empty: key={}", fullKey);
        return false;
    }
}
