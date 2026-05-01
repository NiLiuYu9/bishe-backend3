package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知响应数据
 *
 * 返回站内通知消息的完整信息，包括通知类型、关联业务和已读状态
 */
@Data
public class NotificationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通知ID */
    private Long id;

    /** 接收通知的用户ID */
    private Long userId;

    /** 通知类型 */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 关联业务ID */
    private Long relatedId;

    /** 关联业务类型 */
    private String relatedType;

    /** 是否已读（0-未读，1-已读） */
    private Integer isRead;

    /** 创建时间 */
    private LocalDateTime createTime;

}
