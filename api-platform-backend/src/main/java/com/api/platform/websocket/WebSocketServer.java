package com.api.platform.websocket;

import com.api.platform.constants.SessionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/ws", configurator = WebSocketHandshakeInterceptor.class)
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    private static final ConcurrentHashMap<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();

    private static WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    public static void setWebSocketHandshakeInterceptor(WebSocketHandshakeInterceptor interceptor) {
        webSocketHandshakeInterceptor = interceptor;
    }

    @OnOpen
    public void onOpen(Session session) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        if (userId == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            } catch (IOException e) {
                log.error("close session error", e);
            }
            return;
        }
        Session existingSession = SESSION_MAP.get(userId);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                existingSession.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Replaced by new connection"));
            } catch (IOException e) {
                log.error("close existing session error", e);
            }
        }
        SESSION_MAP.put(userId, session);
        log.info("WebSocket connected, userId: {}, sessionId: {}", userId, session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        if (userId != null) {
            Session storedSession = SESSION_MAP.get(userId);
            if (storedSession != null && storedSession.getId().equals(session.getId())) {
                SESSION_MAP.remove(userId);
            }
            log.info("WebSocket disconnected, userId: {}, sessionId: {}", userId, session.getId());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        log.info("WebSocket message received, userId: {}, message: {}", userId, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        log.error("WebSocket error, userId: {}, sessionId: {}", userId, session.getId(), error);
    }

    public static boolean sendMessage(Long userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if (session == null || !session.isOpen()) {
            return false;
        }
        try {
            session.getBasicRemote().sendText(message);
            return true;
        } catch (IOException e) {
            log.error("send message error, userId: {}", userId, e);
            return false;
        }
    }

    public static boolean isOnline(Long userId) {
        Session session = SESSION_MAP.get(userId);
        return session != null && session.isOpen();
    }

    public static int getOnlineCount() {
        return SESSION_MAP.size();
    }
}
