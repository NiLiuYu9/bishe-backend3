package com.api.platform.service;

import com.api.platform.dto.NotificationQueryDTO;
import com.api.platform.vo.NotificationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 通知服务接口 —— 定义系统通知相关的业务操作
 *
 * 所属业务模块：通知管理模块
 * 包括通知发送、批量发送、标记已读、未读查询、WebSocket推送等功能
 * 实现类为 NotificationServiceImpl
 */
public interface NotificationService {

    /**
     * 发送单条通知
     *
     * 创建通知记录，并通过 WebSocket 实时推送给目标用户
     *
     * @param userId      目标用户 ID
     * @param type        通知类型（如订单通知、审核通知、系统通知等）
     * @param title       通知标题
     * @param content     通知内容
     * @param relatedId   关联业务 ID（如订单 ID、需求 ID 等）
     * @param relatedType 关联业务类型（如 order、requirement 等）
     */
    void sendNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType);

    /**
     * 批量发送通知
     *
     * 向多个用户发送相同内容的通知
     *
     * @param userIds     目标用户 ID 列表
     * @param type        通知类型
     * @param title       通知标题
     * @param content     通知内容
     * @param relatedId   关联业务 ID
     * @param relatedType 关联业务类型
     */
    void sendNotificationBatch(List<Long> userIds, String type, String title, String content, Long relatedId, String relatedType);

    /**
     * 标记单条通知为已读
     *
     * @param userId         用户 ID
     * @param notificationId 通知 ID
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 标记指定类型的所有通知为已读
     *
     * @param userId 用户 ID
     * @param type   通知类型，为 null 则标记所有类型
     */
    void markAllAsRead(Long userId, String type);

    /**
     * 查询未读通知列表
     *
     * @param userId   用户 ID
     * @param queryDTO 查询条件（类型、分页参数）
     * @return IPage<NotificationVO> 分页未读通知列表
     */
    IPage<NotificationVO> getUnreadList(Long userId, NotificationQueryDTO queryDTO);

    /**
     * 查询所有通知列表（含已读和未读）
     *
     * @param userId   用户 ID
     * @param queryDTO 查询条件（类型、分页参数）
     * @return IPage<NotificationVO> 分页通知列表
     */
    IPage<NotificationVO> getAllList(Long userId, NotificationQueryDTO queryDTO);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户 ID
     * @return Long 未读通知数量
     */
    Long getUnreadCount(Long userId);

}
