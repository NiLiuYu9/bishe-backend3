package com.api.platform.controller;

import com.api.platform.annotation.RateLimit;
import com.api.platform.common.Result;
import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.service.AlipayService;
import com.api.platform.service.OrderInfoService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.OrderVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private AlipayService alipayService;

    @PostMapping("/create")
    @RateLimit(capacity = 10, refillRate = 1, message = "下单请求过于频繁，请稍后再试")
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

    @PostMapping("/pay/{id}")
    public Result<String> payOrder(@PathVariable Long id, HttpSession session) {
        log.info("发起支付请求, orderId={}", id);
        Long userId = SessionUtils.getCurrentUserId(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            log.warn("订单不存在, orderId={}", id);
            return Result.failed("订单不存在");
        }
        if (!orderVO.getBuyerId().equals(userId)) {
            log.warn("无权限操作该订单, orderId={}, userId={}, buyerId={}", id, userId, orderVO.getBuyerId());
            return Result.failed("无权限操作该订单");
        }
        if (!"pending".equals(orderVO.getStatus())) {
            log.warn("订单状态不允许支付, orderId={}, status={}", id, orderVO.getStatus());
            return Result.failed("订单状态不允许支付");
        }
        try {
            log.info("调用支付宝创建支付, orderNo={}, price={}", orderVO.getOrderNo(), orderVO.getPrice());
            String form = alipayService.createPayment(
                    id,
                    orderVO.getOrderNo(),
                    "API调用服务-" + orderVO.getApiName(),
                    orderVO.getPrice().toString()
            );
            log.info("支付表单生成成功, orderId={}", id);
            return Result.success(form);
        } catch (Exception e) {
            log.error("发起支付失败, orderId={}", id, e);
            return Result.failed("发起支付失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/pay/notify", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handlePayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        
        boolean result = alipayService.verifyAndProcessNotify(params);
        return result ? "success" : "failure";
    }

    @GetMapping("/pay/query/{id}")
    public Result<Map<String, Object>> queryPayStatus(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        if (!orderVO.getBuyerId().equals(userId)) {
            return Result.failed("无权限操作该订单");
        }
        try {
            String tradeStatus = alipayService.queryPaymentStatus(orderVO.getOrderNo());
            Map<String, Object> result = new HashMap<>();
            result.put("tradeStatus", tradeStatus);
            result.put("orderStatus", orderVO.getStatus());
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("查询支付状态失败: " + e.getMessage());
        }
    }

}
