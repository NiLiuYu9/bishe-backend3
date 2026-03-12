package com.api.platform.vo;

import lombok.Data;

@Data
public class QuotaCheckVO {

    private Long userId;

    private Long apiId;

    private Boolean hasQuota;

    private Integer totalCount;

    private Integer usedCount;

    private Integer remainingCount;

}
