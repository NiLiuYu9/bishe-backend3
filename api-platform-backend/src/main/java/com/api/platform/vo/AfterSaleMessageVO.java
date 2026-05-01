package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售后消息响应数据
 *
 * 返回售后对话中的单条消息，包括发送者信息和消息内容
 */
@Data
public class AfterSaleMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long id;

    /** 关联售后ID */
    private Long afterSaleId;

    /** 发送者用户ID */
    private Long senderId;

    /** 发送者用户名 */
    private String senderName;

    /** 发送者类型（applicant/developer/admin） */
    private String senderType;

    /** 消息内容 */
    private String content;

    /** 发送时间 */
    private LocalDateTime createTime;

}
