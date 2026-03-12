package com.api.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class StatisticsQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String apiName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long typeId;

    private String status;

    private String timeRange;
}
