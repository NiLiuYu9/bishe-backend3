package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户回复评价参数
 *
 * 用于用户对发布者回复进行再次回复（嵌套回复中的第三层）
 */
@Data
public class ApiReviewUserReplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 回复的目标ID（发布者回复的ID），不能为空 */
    @NotNull(message = "回复ID不能为空")
    private Long replyId;

    /** 回复内容，不能为空 */
    @NotBlank(message = "回复内容不能为空")
    private String content;

}