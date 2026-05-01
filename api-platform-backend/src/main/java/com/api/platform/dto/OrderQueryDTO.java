package com.api.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 订单查询参数
 *
 * 用于订单列表查询接口，支持按订单号、状态、买家ID筛选，继承分页参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单编号，精确匹配 */
    private String orderNo;

    /** 订单状态，可选值：pending/paid/completed/refunded/cancelled */
    private String status;

    /** 买家用户ID，按买家筛选 */
    private Long buyerId;

}
