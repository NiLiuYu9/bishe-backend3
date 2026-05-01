package com.api.platform.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建订单请求参数
 *
 * 用于 /order/create 接口，接收购买API时提交的订单信息
 */
@Data
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API ID，不能为空，指定要购买的API */
    @NotNull(message = "API ID不能为空")
    private Long apiId;

    /** 调用次数，不能为空，最小为1 */
    @NotNull(message = "调用次数不能为空")
    @Min(value = 1, message = "调用次数最小为1")
    private Integer invokeCount;

}
