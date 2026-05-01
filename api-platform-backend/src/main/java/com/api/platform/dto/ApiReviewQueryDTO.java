package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 评价查询参数
 *
 * 用于评价列表查询接口，支持按API、用户、父评论、回复类型筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiReviewQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API ID，按指定API筛选评价 */
    private Long apiId;

    /** 用户ID，按评价者筛选 */
    private Long userId;

    /** 父评论ID，查询某条评价的回复 */
    private Long parentReviewId;

    /** 回复类型，0-用户回复，1-发布者回复，2-用户对发布者的回复 */
    private Integer replyType;

    /** 是否包含回复，true-包含嵌套回复 */
    private Boolean includeReplies;

    /** 是否仅查询原始评价（不含回复），true-仅查询顶级评价 */
    private Boolean queryOriginalOnly;

}
