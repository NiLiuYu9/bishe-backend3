package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 通知查询参数
 *
 * 用于通知列表查询接口，支持按类型筛选和分页
 */
@Data
public class NotificationQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，最小为1，默认1 */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /** 每页条数，最小1，最大100，默认10 */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页最多查询100条")
    private Integer pageSize = 10;

    /** 消息类型，按类型筛选 */
    private String type;

}
