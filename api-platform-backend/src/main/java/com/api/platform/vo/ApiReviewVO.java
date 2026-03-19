package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApiReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderId;

    private Long apiId;

    private String apiName;

    private Long userId;

    private String username;

    private BigDecimal rating;

    private String content;

    private String reply;

    private LocalDateTime replyTime;

    private Long parentId;

    private Integer replyType;

    private LocalDateTime createTime;

    private List<ApiReviewVO> replies;

}
