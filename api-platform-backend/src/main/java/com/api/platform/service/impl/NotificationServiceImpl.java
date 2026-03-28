package com.api.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.api.platform.dto.NotificationQueryDTO;
import com.api.platform.entity.Notification;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.NotificationMapper;
import com.api.platform.service.NotificationService;
import com.api.platform.vo.NotificationVO;
import com.api.platform.websocket.WebSocketServer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void sendNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setIsRead(0);
        save(notification);

        pushNotification(userId, notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotificationBatch(List<Long> userIds, String type, String title, String content, Long relatedId, String relatedType) {
        for (Long userId : userIds) {
            sendNotification(userId, type, title, content, relatedId, relatedType);
        }
    }

    @Override
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "消息不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限操作该消息");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, 1);
        update(updateWrapper);
    }

    @Override
    public void markAllAsRead(Long userId, String type) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        if (StringUtils.hasText(type)) {
            updateWrapper.eq(Notification::getType, type);
        }
        updateWrapper.set(Notification::getIsRead, 1);
        update(updateWrapper);
    }

    @Override
    public IPage<NotificationVO> getUnreadList(Long userId, NotificationQueryDTO queryDTO) {
        Page<Notification> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        if (StringUtils.hasText(queryDTO.getType())) {
            queryWrapper.eq(Notification::getType, queryDTO.getType());
        }
        queryWrapper.orderByDesc(Notification::getCreateTime);

        IPage<Notification> notificationPage = page(page, queryWrapper);
        return convertToVOPage(notificationPage);
    }

    @Override
    public IPage<NotificationVO> getAllList(Long userId, NotificationQueryDTO queryDTO) {
        Page<Notification> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId);
        if (StringUtils.hasText(queryDTO.getType())) {
            queryWrapper.eq(Notification::getType, queryDTO.getType());
        }
        queryWrapper.orderByDesc(Notification::getCreateTime);

        IPage<Notification> notificationPage = page(page, queryWrapper);
        return convertToVOPage(notificationPage);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return count(queryWrapper);
    }

    private void pushNotification(Long userId, Notification notification) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "notification");
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("id", notification.getId());
        notificationData.put("type", notification.getType());
        notificationData.put("title", notification.getTitle());
        notificationData.put("content", notification.getContent());
        notificationData.put("relatedId", notification.getRelatedId());
        notificationData.put("relatedType", notification.getRelatedType());
        notificationData.put("createTime", notification.getCreateTime());
        data.put("data", notificationData);
        data.put("unreadCount", getUnreadCount(userId));

        String message = JSONUtil.toJsonStr(data);
        WebSocketServer.sendMessage(userId, message);
    }

    private IPage<NotificationVO> convertToVOPage(IPage<Notification> notificationPage) {
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);
        return vo;
    }

}
