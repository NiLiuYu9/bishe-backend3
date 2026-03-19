package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.service.OrderInfoService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.OrderVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/create")
    public Result<OrderVO> createOrder(@Validated @RequestBody OrderCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        String username = SessionUtils.getCurrentUsername(session);
        OrderVO orderVO = orderInfoService.createOrder(userId, username, createDTO);
        return Result.success(orderVO);
    }

    @GetMapping("/list")
    public Result<PageResultVO<OrderVO>> getMyOrders(OrderQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        queryDTO.setBuyerId(userId);
        IPage<OrderVO> orderVOPage = orderInfoService.pageOrderList(queryDTO);
        return Result.success(PageResultVO.of(orderVOPage.getRecords(), orderVOPage.getTotal()));
    }

    @GetMapping("/detail/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        if (!orderVO.getBuyerId().equals(userId)) {
            return Result.failed("无权限查看该订单");
        }
        return Result.success(orderVO);
    }

    @PutMapping("/update-status/{id}")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        if (!orderVO.getBuyerId().equals(userId)) {
            return Result.failed("无权限操作该订单");
        }
        orderInfoService.updateOrderStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        if (!orderVO.getBuyerId().equals(userId)) {
            return Result.failed("无权限操作该订单");
        }
        orderInfoService.deleteOrder(id);
        return Result.success();
    }

    @PostMapping("/rate/{id}")
    public Result<Void> rateOrder(@PathVariable Long id, @Validated @RequestBody OrderRatingDTO ratingDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        orderInfoService.rateOrder(id, userId, ratingDTO);
        return Result.success();
    }

}
