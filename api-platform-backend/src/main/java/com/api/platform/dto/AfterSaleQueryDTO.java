package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AfterSaleQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long requirementId;

    private Long applicantId;

    private Long developerId;

    private String status;

}
