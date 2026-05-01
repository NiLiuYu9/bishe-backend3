package com.api.platform.vo;

import lombok.Data;

/**
 * 配额检查响应数据
 *
 * 返回用户对指定API的配额检查结果，包括总额度、已用额度和剩余额度
 */
@Data
public class QuotaCheckVO {

    /** 用户ID */
    private Long userId;

    /** API ID */
    private Long apiId;

    /** 是否有剩余配额 */
    private Boolean hasQuota;

    /** 总调用次数额度 */
    private Integer totalCount;

    /** 已使用调用次数 */
    private Integer usedCount;

    /** 剩余调用次数 */
    private Integer remainingCount;

}
