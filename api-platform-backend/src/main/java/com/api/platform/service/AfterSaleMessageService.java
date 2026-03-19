package com.api.platform.service;

import com.api.platform.entity.AfterSaleMessage;
import com.api.platform.vo.AfterSaleMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AfterSaleMessageService extends IService<AfterSaleMessage> {

    List<AfterSaleMessage> getMessagesByAfterSaleId(Long afterSaleId);

    AfterSaleMessage sendMessage(Long afterSaleId, Long senderId, String senderType, String content);

    List<AfterSaleMessageVO> getMessagesWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin);

    AfterSaleMessageVO sendMessageWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin, String content);

}
