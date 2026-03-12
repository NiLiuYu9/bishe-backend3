package com.api.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuotaVO {

    private Long id;

    private Long apiId;

    private String apiName;

    private Integer totalCount;

    private Integer usedCount;

    private Integer remainingCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
