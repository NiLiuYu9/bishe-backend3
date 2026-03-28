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

@Component
public class WebSocketHandshakeInterceptor extends ServerEndpointConfig.Configurator {

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WebSocketHandshakeInterceptor.stringRedisTemplate = stringRedisTemplate;
    }

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
