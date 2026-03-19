package com.api.platform.vo;

import com.api.platform.dto.ApiParamDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequirementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String username;

    private String title;

    private String description;

    private List<ApiParamDTO> requestParams;

    private List<ApiParamDTO> responseParams;

    private BigDecimal budget;

    private LocalDateTime deadline;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<ApplicantVO> applicants;

    private ApplicantVO selectedApplicant;

    private String myApplyStatus;

    private String deliveryUrl;

}
