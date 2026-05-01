package com.api.platform.service;

import com.api.platform.entity.AfterSaleMessage;
import com.api.platform.vo.AfterSaleMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 售后消息服务接口 —— 定义售后对话消息的业务操作
 *
 * 所属业务模块：售后管理模块
 * 包括发送消息、查询对话记录等功能，支持售后过程中的多方沟通
 * 实现类为 AfterSaleMessageServiceImpl
 */
public interface AfterSaleMessageService extends IService<AfterSaleMessage> {

    /**
     * 根据售后ID查询消息列表
     *
     * @param afterSaleId 售后 ID
     * @return List<AfterSaleMessage> 消息列表
     */
    List<AfterSaleMessage> getMessagesByAfterSaleId(Long afterSaleId);

    /**
     * 发送售后消息
     *
     * 在售后对话中发送一条消息
     *
     * @param afterSaleId 售后 ID
     * @param senderId    发送者用户 ID
     * @param senderType  发送者类型（applicant、developer、admin）
     * @param content     消息内容
     * @return AfterSaleMessage 发送后的消息实体
     */
    AfterSaleMessage sendMessage(Long afterSaleId, Long senderId, String senderType, String content);

    /**
     * 查询售后消息列表（带权限校验）
     *
     * 仅售后相关方（申请者、开发者、管理员）可查看
     *
     * @param afterSaleId 售后 ID
     * @param userId      当前用户 ID
     * @param isAdmin     是否为管理员
     * @return List<AfterSaleMessageVO> 消息列表（含发送者信息）
     */
    List<AfterSaleMessageVO> getMessagesWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin);

    /**
     * 发送售后消息（带权限校验）
     *
     * 仅售后相关方（申请者、开发者、管理员）可发送消息
     *
     * @param afterSaleId 售后 ID
     * @param userId      当前用户 ID
     * @param isAdmin     是否为管理员
     * @param content     消息内容
     * @return AfterSaleMessageVO 发送后的消息信息（含发送者信息）
     */
    AfterSaleMessageVO sendMessageWithPermissionCheck(Long afterSaleId, Long userId, boolean isAdmin, String content);

}
