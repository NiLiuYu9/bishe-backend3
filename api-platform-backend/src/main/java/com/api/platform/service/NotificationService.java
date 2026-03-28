package com.api.platform.service;

import com.api.platform.dto.NotificationQueryDTO;
import com.api.platform.vo.NotificationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface NotificationService {

    void sendNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType);

    void sendNotificationBatch(List<Long> userIds, String type, String title, String content, Long relatedId, String relatedType);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId, String type);

    IPage<NotificationVO> getUnreadList(Long userId, NotificationQueryDTO queryDTO);

    IPage<NotificationVO> getAllList(Long userId, NotificationQueryDTO queryDTO);

    Long getUnreadCount(Long userId);

}
