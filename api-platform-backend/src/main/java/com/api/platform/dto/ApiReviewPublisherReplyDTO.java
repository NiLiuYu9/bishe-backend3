package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 发布者回复评价参数
 *
 * 用于API发布者对用户的评价进行回复（嵌套回复中的第二层）
 */
@Data
public class ApiReviewPublisherReplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论ID（原始评价的ID），不能为空 */
    @NotNull(message = "评论ID不能为空")
    private Long reviewId;

    /** 回复内容，不能为空 */
    @NotBlank(message = "回复内容不能为空")
    private String content;

}