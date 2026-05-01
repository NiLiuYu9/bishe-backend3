package com.api.platform.service.impl;

import com.api.platform.constants.NotificationType;
import com.api.platform.entity.AfterSaleMessage;
import com.api.platform.entity.RequirementAfterSale;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.AfterSaleMessageMapper;
import com.api.platform.mapper.RequirementAfterSaleMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.AfterSaleMessageService;
import com.api.platform.service.NotificationService;
import com.api.platform.vo.AfterSaleMessageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 售后消息服务实现 —— 处理售后对话消息的发送和查询
 *
 * 售后对话中包含三方角色：申请人(applicant)、开发者(developer)、管理员(admin)
 * 每条消息记录发送者ID和发送者类型，用于区分消息来源
 */
@Service
public class AfterSaleMessageServiceImpl extends ServiceImpl<AfterSaleMessageMapper, AfterSaleMessage> implements AfterSaleMessageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RequirementAfterSaleMapper afterSaleMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<AfterSaleMessage> getMessagesByAfterSaleId(Long afterSaleId) {
        List<AfterSaleMessage> messages = this.baseMapper.selectList(new LambdaQueryWrapper<AfterSaleMessage>()
                .eq(AfterSaleMessage::getAfterSaleId, afterSaleId)
                .orderByAsc(AfterSaleMessage::getCreateTime));
        
        if (messages.isEmpty()) {
            return messages;
        }
        
        List<Long> senderIds = messages.stream()
                .map(AfterSaleMessage::getSenderId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!senderIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(senderIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        
        Map<Long, String> finalUsernameMap = usernameMap;
        messages.forEach(msg -> msg.setSenderName(finalUsernameMap.get(msg.getSenderId())));
        
        return messages;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfterSaleMessage sendMessage(Long afterSaleId, Long senderId, String senderType, String content) {
        AfterSaleMessage message = new AfterSaleMessage();
        message.setAfterSaleId(afterSaleId);
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setContent(content);
        save(message);
        
        User user = userMapper.selectById(senderId);
        if (user != null) {
            message.setSenderName(user.getUsername());
        }
        
        return message;
    }

    @Override
    public List<AfterSaleMessageVO> getMessagesWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin) {
        RequirementAfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }
        if (!afterSale.getApplicantId().equals(userId) && !afterSale.getDeveloperId().equals(userId) && !isAdmin) {
            throw new BusinessException(403, "无权限查看该售后对话");
        }
        
        List<AfterSaleMessage> messages = getMessagesByAfterSaleId(afterSaleId);
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public AfterSaleMessageVO sendMessageWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin, String content) {
        RequirementAfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }
        if (!"pending".equals(afterSale.getStatus())) {
            throw new BusinessException(400, "该售后申请已处理，无法继续对话");
        }
        
        String senderType;
        if (isAdmin) {
            senderType = "admin";
        } else if (afterSale.getApplicantId().equals(userId)) {
            senderType = "applicant";
        } else if (afterSale.getDeveloperId().equals(userId)) {
            senderType = "developer";
        } else {
            throw new BusinessException(403, "无权限发送消息");
        }
        
        AfterSaleMessage message = sendMessage(afterSaleId, userId, senderType, content);
        if (!userId.equals(afterSale.getApplicantId())) {
            notificationService.sendNotification(
                afterSale.getApplicantId(),
                NotificationType.AFTER_SALE_NEW_MESSAGE.getCode(),
                "售后新消息",
                "您的售后申请有新消息",
                afterSaleId,
                "after_sale"
            );
        }
        if (!userId.equals(afterSale.getDeveloperId())) {
            notificationService.sendNotification(
                afterSale.getDeveloperId(),
                NotificationType.AFTER_SALE_NEW_MESSAGE.getCode(),
                "售后新消息",
                "您收到的售后申请有新消息",
                afterSaleId,
                "after_sale"
            );
        }
        return convertToVO(message);
    }

    private AfterSaleMessageVO convertToVO(AfterSaleMessage message) {
        AfterSaleMessageVO vo = new AfterSaleMessageVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

}
