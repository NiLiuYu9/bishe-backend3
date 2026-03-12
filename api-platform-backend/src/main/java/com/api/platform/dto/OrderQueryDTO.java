package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String status;

    private Long buyerId;

}
