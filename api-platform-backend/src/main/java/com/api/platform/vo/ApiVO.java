package com.api.platform.vo;

import com.api.platform.dto.ApiParamDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApiVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private String typeName;

    private Long typeId;

    private Long userId;

    private String username;

    private String method;

    private String endpoint;

    private String targetUrl;

    private List<ApiParamDTO> requestParams;

    private List<ApiParamDTO> responseParams;

    private String requestParamsJson;

    private String responseParamsJson;

    private BigDecimal price;

    private String priceUnit;

    private Integer callLimit;

    private Integer whitelistEnabled;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String docUrl;

    private BigDecimal rating;

    private Long invokeCount;

    private Long successCount;

    private Long failCount;

    private Boolean isFavorited;

}
