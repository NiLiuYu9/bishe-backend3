package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiReviewQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long apiId;

    private Long userId;

    private Long parentReviewId;

    private Integer replyType;

    private Boolean includeReplies;

    private Boolean queryOriginalOnly;

}
