package com.api.platform.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型枚举
 * <p>核心职责：定义系统中所有通知类型的编码和描述，
 * 用于创建通知时指定类型，前端根据类型展示不同的通知样式。</p>
 */
@Getter
@AllArgsConstructor
public enum NotificationType {

    /** 需求新消息（需求方/开发者发送新消息） */
    REQUIREMENT_NEW_MESSAGE("requirement_new_message", "需求新消息"),
    /** 需求状态更新（需求状态变更通知） */
    REQUIREMENT_STATUS_UPDATE("requirement_status_update", "需求状态更新"),
    /** 售后新消息（售后沟通新消息） */
    AFTER_SALE_NEW_MESSAGE("after_sale_new_message", "售后新消息"),
    /** 售后状态更新（售后状态变更通知） */
    AFTER_SALE_STATUS_UPDATE("after_sale_status_update", "售后状态更新"),
    /** API评价回复（发布者回复用户评价） */
    API_REVIEW_REPLY("api_review_reply", "API评价回复"),
    /** API新评论（用户对API发表新评价） */
    API_NEW_REVIEW("api_new_review", "API新评论");

    /** 通知类型编码 */
    private final String code;
    /** 通知类型描述 */
    private final String description;

}
