package com.api.platform.constants;

/**
 * API缓存常量
 * <p>核心职责：定义API相关缓存的Key前缀和过期时间，
 * 统一管理缓存Key命名规范，避免Key冲突。</p>
 */
public class ApiCacheConstant {
    
    /** API详情缓存Key前缀（api:info:{apiId}） */
    public static final String API_INFO_KEY = "api:info:";
    
    /** API路径映射缓存Key前缀（api:path:{endpoint}:{method} → apiId） */
    public static final String API_PATH_KEY = "api:path:";
    
    /** API列表缓存Key前缀（api:list:{queryHash}） */
    public static final String API_LIST_KEY = "api:list:";
    
    /** API空值缓存Key前缀（api:null:{apiId}，防缓存穿透） */
    public static final String API_NULL_KEY = "api:null:";
    
    /** 限流缓存Key前缀 */
    public static final String RATE_LIMIT_KEY = "rate_limit:";
    
    /** API列表缓存过期时间（秒），默认5分钟 */
    public static final long API_LIST_EXPIRE = 300;
    
    /** API空值缓存过期时间（秒），默认2分钟 */
    public static final long API_NULL_EXPIRE = 120;
    
    private ApiCacheConstant() {
    }
}
