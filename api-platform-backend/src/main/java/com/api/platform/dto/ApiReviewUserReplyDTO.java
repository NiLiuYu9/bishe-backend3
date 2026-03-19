package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ApiReviewUserReplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "回复ID不能为空")
    private Long replyId;

    @NotBlank(message = "回复内容不能为空")
    private String content;

}