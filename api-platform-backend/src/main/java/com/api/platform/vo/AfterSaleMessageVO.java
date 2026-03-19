package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AfterSaleMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long afterSaleId;

    private Long senderId;

    private String senderName;

    private String senderType;

    private String content;

    private LocalDateTime createTime;

}
