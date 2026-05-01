package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新评价参数
 *
 * 用于用户修改自己已发布的评价内容
 */
@Data
public class ApiReviewUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论ID，不能为空，指定要修改的评价 */
    @NotNull(message = "评论ID不能为空")
    private Long reviewId;

    /** 修改后的评论内容，不能为空 */
    @NotBlank(message = "评论内容不能为空")
    private String content;

}