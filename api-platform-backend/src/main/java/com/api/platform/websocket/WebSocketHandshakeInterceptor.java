package com.api.platform.websocket;

import com.api.platform.constants.SessionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

/**
 * WebSocket握手拦截器
 * <p>核心职责：在WebSocket握手阶段，从HTTP Cookie中提取SESSION ID，
 * 再从Redis中查找对应的用户ID，注入到WebSocket会话的用户属性中，
 * 实现WebSocket连接的身份认证。</p>
 */
@Component
public class WebSocketHandshakeInterceptor extends ServerEndpointConfig.Configurator {

    /** 静态注入RedisTemplate（WebSocket端点不由Spring管理实例） */
    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WebSocketHandshakeInterceptor.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 修改握手配置，注入用户ID
     * <p>1. 从请求Cookie中提取SESSION ID；
     * 2. 根据SESSION ID从Redis中查找用户ID；
     * 3. 将用户ID注入到WebSocket会话的用户属性中。</p>
     *
     * @param sec      服务端端点配置
     * @param request  握手请求
     * @param response 握手响应
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, Object> userProperties = sec.getUserProperties();
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        if (cookieHeaders != null && !cookieHeaders.isEmpty()) {
            String cookieHeader = cookieHeaders.get(0);
            String sessionId = extractSessionId(cookieHeader);
            if (sessionId != null) {
                Long userId = getUserIdFromSession(sessionId);
                if (userId != null) {
                    userProperties.put(SessionConstants.USER_ID, userId);
                }
            }
        }
    }

    /**
     * 从Cookie头中提取SESSION ID
     *
     * @param cookieHeader Cookie请求头字符串
     * @return SESSION ID，未找到时返回null
     */
    private String extractSessionId(String cookieHeader) {
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String trimmed = cookie.trim();
            if (trimmed.startsWith("SESSION=")) {
                return trimmed.substring("SESSION=".length());
            }
        }
        return null;
    }

    /**
     * 根据SESSION ID从Redis中获取用户ID
     * <p>Spring Session将数据存储在Redis的Hash结构中，
     * 通过spring:session:sessions:{sessionId}键和sessionAttr:userId字段获取用户ID。</p>
     *
     * @param sessionId HTTP Session ID
     * @return 用户ID，未找到时返回null
     */
    private Long getUserIdFromSession(String sessionId) {
        if (stringRedisTemplate == null) {
            return null;
        }
        String sessionKey = "spring:session:sessions:" + sessionId;
        String userIdStr = (String) stringRedisTemplate.opsForHash().get(sessionKey, "sessionAttr:" + SessionConstants.USER_ID);
        if (userIdStr != null) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
