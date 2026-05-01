package com.api.platform.constants;

/**
 * Session常量
 * <p>核心职责：定义HttpSession中存储的用户属性Key名称，
 * 统一Session属性命名，避免硬编码字符串。</p>
 */
public final class SessionConstants {

    private SessionConstants() {
    }

    /** Session中存储的用户ID属性Key */
    public static final String USER_ID = "userId";
    /** Session中存储的用户名属性Key */
    public static final String USERNAME = "username";
    /** Session中存储的用户对象属性Key */
    public static final String USER = "user";

}
