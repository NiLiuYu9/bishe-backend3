package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API评价响应数据
 *
 * 返回API评价的完整信息，支持嵌套回复结构（parentId + replyType）
 */
@Data
public class ApiReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long id;

    /** 关联订单ID */
    private Long orderId;

    /** 关联API ID */
    private Long apiId;

    /** 关联API名称 */
    private String apiName;

    /** 评价者用户ID */
    private Long userId;

    /** 评价者用户名 */
    private String username;

    /** 评分（1-5） */
    private BigDecimal rating;

    /** 评价内容 */
    private String content;

    /** 回复内容 */
    private String reply;

    /** 回复时间 */
    private LocalDateTime replyTime;

    /** 父评价ID（用于嵌套回复） */
    private Long parentId;

    /** 回复类型（0-评价，1-开发者回复，2-用户追评） */
    private Integer replyType;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 子回复列表 */
    private List<ApiReviewVO> replies;

}
