package com.api.platform.service;

import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.entity.OrderInfo;
import com.api.platform.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * 订单服务接口 —— 定义订单相关的核心业务操作
 *
 * 所属业务模块：订单管理模块
 * 包括订单创建、查询、状态变更、删除、评分等功能
 * 实现类为 OrderInfoServiceImpl
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 创建订单
     *
     * 用户购买API时创建订单，初始状态为待支付
     *
     * @param userId   购买者用户 ID
     * @param username 购买者用户名
     * @param createDTO 订单创建表单（API ID、购买数量等）
     * @return OrderVO 创建后的订单信息
     */
    OrderVO createOrder(Long userId, String username, OrderCreateDTO createDTO);

    /**
     * 分页查询订单列表
     *
     * 支持按订单号、用户、API、状态等条件筛选
     *
     * @param queryDTO 订单查询条件（订单号、状态、分页参数）
     * @return IPage<OrderVO> 分页订单信息列表
     */
    IPage<OrderVO> pageOrderList(OrderQueryDTO queryDTO);

    /**
     * 获取订单详情
     *
     * @param orderId 订单 ID
     * @return OrderVO 订单详细信息
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 更新订单状态
     *
     * 订单状态流转：待支付 → 已支付 → 已完成 / 已取消
     *
     * @param orderId 订单 ID
     * @param status  目标状态
     */
    void updateOrderStatus(Long orderId, String status);

    /**
     * 删除订单
     *
     * 仅允许删除已取消或已完成的订单
     *
     * @param orderId 订单 ID
     */
    void deleteOrder(Long orderId);

    /**
     * 订单评分
     *
     * 用户对已完成的订单进行评分
     *
     * @param orderId   订单 ID
     * @param userId    评分用户 ID
     * @param ratingDTO 评分表单（评分值、评价内容）
     */
    void rateOrder(Long orderId, Long userId, OrderRatingDTO ratingDTO);

    /**
     * 根据API ID获取平均评分
     *
     * @param apiId API ID
     * @return BigDecimal 平均评分，无评分时返回 null
     */
    BigDecimal getAverageRatingByApiId(Long apiId);

}
