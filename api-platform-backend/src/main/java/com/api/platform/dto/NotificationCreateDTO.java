package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通知创建参数
 *
 * 用于创建系统通知消息，指定接收用户、消息类型和内容
 */
@Data
public class NotificationCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 接收通知的用户ID，不能为空 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 消息类型，不能为空，如system/order/requirement等 */
    @NotBlank(message = "消息类型不能为空")
    private String type;

    /** 消息标题，不能为空 */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /** 消息内容，不能为空 */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /** 关联业务ID，如订单ID、需求ID等 */
    private Long relatedId;

    /** 关联业务类型，如order/requirement等 */
    private String relatedType;

}
