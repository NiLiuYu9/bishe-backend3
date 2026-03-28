package com.api.platform.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    REQUIREMENT_NEW_MESSAGE("requirement_new_message", "需求新消息"),
    REQUIREMENT_STATUS_UPDATE("requirement_status_update", "需求状态更新"),
    AFTER_SALE_NEW_MESSAGE("after_sale_new_message", "售后新消息"),
    AFTER_SALE_STATUS_UPDATE("after_sale_status_update", "售后状态更新"),
    API_REVIEW_REPLY("api_review_reply", "API评价回复"),
    API_NEW_REVIEW("api_new_review", "API新评论");

    private final String code;
    private final String description;

}
