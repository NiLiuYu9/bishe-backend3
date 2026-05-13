package com.api.platform.websocket;

import com.api.platform.constants.SessionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务端
 * 核心职责：管理WebSocket连接的生命周期（连接/断开/消息/异常），
 * 维护userId→Session的映射，支持向指定用户推送实时消息。
 * 同一用户只保留最新的连接（新连接会替换旧连接）。
 */
@ServerEndpoint(value = "/ws", configurator = WebSocketHandshakeInterceptor.class)
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /** 用户ID → WebSocket会话映射（线程安全） */
    private static final ConcurrentHashMap<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();

    private static WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    public static void setWebSocketHandshakeInterceptor(WebSocketHandshakeInterceptor interceptor) {
        webSocketHandshakeInterceptor = interceptor;
    }

    /**
     * 连接建立时触发
     * <p>1. 从用户属性中获取userId（由握手拦截器注入）；
     * 2. 未认证则关闭连接；
     * 3. 同一用户已有连接则关闭旧连接（单设备登录策略）；
     * 4. 将新连接注册到SESSION_MAP。</p>
     *
     * @param session WebSocket会话
     */
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
        // 单设备登录：关闭同一用户的旧连接
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

    /**
     * 连接关闭时触发
     * <p>从SESSION_MAP中移除对应用户的会话（仅当sessionId匹配时才移除，避免误删新连接）。</p>
     *
     * @param session WebSocket会话
     */
    @OnClose
    public void onClose(Session session) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        if (userId != null) {
            Session storedSession = SESSION_MAP.get(userId);
            // 仅当存储的session与当前session一致时才移除，避免新连接被误删
            if (storedSession != null && storedSession.getId().equals(session.getId())) {
                SESSION_MAP.remove(userId);
            }
            log.info("WebSocket disconnected, userId: {}, sessionId: {}", userId, session.getId());
        }
    }

    /**
     * 收到客户端消息时触发
     * <p>目前仅记录日志，未实现业务逻辑。</p>
     *
     * @param message 客户端发送的消息
     * @param session WebSocket会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        log.info("WebSocket message received, userId: {}, message: {}", userId, message);
    }

    /**
     * 发生错误时触发
     *
     * @param session WebSocket会话
     * @param error   异常信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        Long userId = (Long) session.getUserProperties().get(SessionConstants.USER_ID);
        log.error("WebSocket error, userId: {}, sessionId: {}", userId, session.getId(), error);
    }

    /**
     * 向指定用户发送消息
     * <p>用于通知服务主动推送消息给在线用户（如新通知、状态变更等）。</p>
     *
     * @param userId  目标用户ID
     * @param message 消息内容
     * @return true-发送成功，false-用户不在线或发送失败
     */
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

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return true-在线，false-离线
     */
    public static boolean isOnline(Long userId) {
        Session session = SESSION_MAP.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取当前在线用户数
     *
     * @return 在线用户数
     */
    public static int getOnlineCount() {
        return SESSION_MAP.size();
    }
}
