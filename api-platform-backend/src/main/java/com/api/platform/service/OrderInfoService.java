package com.api.platform.service;

import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.entity.OrderInfo;
import com.api.platform.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface OrderInfoService extends IService<OrderInfo> {

    OrderVO createOrder(Long userId, String username, OrderCreateDTO createDTO);

    IPage<OrderVO> pageOrderList(OrderQueryDTO queryDTO);

    OrderVO getOrderDetail(Long orderId);

    void updateOrderStatus(Long orderId, String status);

    void deleteOrder(Long orderId);

    void rateOrder(Long orderId, Long userId, OrderRatingDTO ratingDTO);

    BigDecimal getAverageRatingByApiId(Long apiId);

}
