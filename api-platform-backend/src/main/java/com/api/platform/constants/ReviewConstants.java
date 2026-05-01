package com.api.platform.constants;

/**
 * 评价常量
 * <p>核心职责：定义API评价的类型常量，区分原始评价和回复类型。</p>
 */
public class ReviewConstants {

    /** 原始评价（用户首次发表的评价） */
    public static final Integer REVIEW_TYPE_ORIGINAL = 0;

    /** 发布者回复（API发布者对用户评价的回复） */
    public static final Integer REVIEW_TYPE_PUBLISHER_REPLY = 1;

    /** 用户追评（用户对发布者回复的追加评论） */
    public static final Integer REVIEW_TYPE_USER_REPLY = 2;

}