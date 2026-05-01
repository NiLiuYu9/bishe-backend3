package com.api.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户配额响应数据
 *
 * 返回用户对指定API的配额详情，包括总额度、已用额度和剩余额度
 */
@Data
public class UserQuotaVO {

    /** 配额记录ID */
    private Long id;

    /** API ID */
    private Long apiId;

    /** API名称 */
    private String apiName;

    /** 总调用次数额度 */
    private Integer totalCount;

    /** 已使用调用次数 */
    private Integer usedCount;

    /** 剩余调用次数 */
    private Integer remainingCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

}
