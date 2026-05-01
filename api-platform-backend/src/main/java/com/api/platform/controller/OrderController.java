package com.api.platform.controller;

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

/**
 * 订单控制器 —— 处理API订单创建、支付、评分及订单管理请求
 *
 * 路由前缀：/order
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 订单状态流转：pending → paid → completed / refunded / cancelled
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private AlipayService alipayService;

    /**
     * 创建订单
     *
     * 用户购买API时创建订单，状态默认为 pending。
     * 创建订单时会自动分配用户配额。
     *
     * @param createDTO 订单创建表单（apiId、购买数量）
     * @param session   HttpSession，用于获取当前登录用户ID和用户名
     * @return Result&lt;OrderVO&gt; 创建成功的订单信息
     */
    @PostMapping("/create")
    public Result<OrderVO> createOrder(@Validated @RequestBody OrderCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        String username = SessionUtils.getCurrentUsername(session);
        OrderVO orderVO = orderInfoService.createOrder(userId, username, createDTO);
        return Result.success(orderVO);
    }

    /**
     * 获取当前用户的订单列表
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;OrderVO&gt;&gt; 分页的订单列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<OrderVO>> getMyOrders(OrderQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        queryDTO.setBuyerId(userId);
        IPage<OrderVO> orderVOPage = orderInfoService.pageOrderList(queryDTO);
        return Result.success(PageResultVO.of(orderVOPage.getRecords(), orderVOPage.getTotal()));
    }

    /**
     * 获取订单详情
     *
     * 仅订单购买者可查看
     *
     * @param id      订单ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;OrderVO&gt; 订单详情
     */
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

    /**
     * 更新订单状态
     *
     * 仅订单购买者可操作，支持取消等状态变更
     *
     * @param id      订单ID
     * @param status  目标状态
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 操作成功无返回数据
     */
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

    /**
     * 删除订单
     *
     * 仅订单购买者可删除
     *
     * @param id      订单ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 删除成功无返回数据
     */
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

    /**
     * 对已完成订单进行评分
     *
     * 仅订单购买者可评分，评分后同时创建API评价记录
     *
     * @param id         订单ID
     * @param ratingDTO  评分表单（评分1-5、评价内容）
     * @param session    HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 评分成功无返回数据
     */
    @PostMapping("/rate/{id}")
    public Result<Void> rateOrder(@PathVariable Long id, @Validated @RequestBody OrderRatingDTO ratingDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        orderInfoService.rateOrder(id, userId, ratingDTO);
        return Result.success();
    }

    /**
     * 发起订单支付
     *
     * 业务流程：
     * 1. 校验订单存在性和操作权限（仅购买者可支付）
     * 2. 校验订单状态必须为 pending（待支付），非 pending 状态不允许支付
     * 3. 调用支付宝沙箱 SDK 创建支付请求，生成支付表单
     * 4. 返回支付宝支付页面 HTML 表单字符串，前端提交该表单即可跳转到支付宝支付页面
     *
     * @param id      订单ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;String&gt; 支付宝支付页面HTML表单字符串，前端需将该字符串写入页面并自动提交
     */
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

    /**
     * 支付宝异步回调通知接口
     *
     * 支付宝在用户支付成功后异步调用此接口通知服务端。
     * 参数解析逻辑：从 HttpServletRequest 中提取所有请求参数，将多值参数用逗号拼接为单值，
     * 构建为 Map&lt;String, String&gt; 交给 AlipayService 进行签名验证和业务处理。
     *
     * 返回值含义：
     * - "success"：通知处理成功，支付宝不再重复通知
     * - "failure"：通知处理失败，支付宝会按规则重试通知
     *
     * produces = TEXT_PLAIN_VALUE 的原因：支付宝回调协议要求服务端返回纯文本（text/plain），
     * 而非 JSON 格式，支付宝根据返回内容判断是否需要重试。
     *
     * @param request HttpServletRequest，包含支付宝回调的所有参数
     * @return "success" 或 "failure" 纯文本字符串
     */
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

    /**
     * 查询订单支付状态
     *
     * 通过支付宝SDK查询交易状态，用于前端轮询支付结果
     *
     * @param id      订单ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Map&lt;String, Object&gt;&gt; 包含 tradeStatus（支付宝交易状态）和 orderStatus（订单状态）
     */
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
            // 兜底：支付宝已成功但本地订单未更新时，主动同步状态
            String orderStatus = orderVO.getStatus();
            if ("TRADE_SUCCESS".equals(tradeStatus) && "pending".equals(orderStatus)) {
                orderInfoService.updateOrderStatus(id, "paid");
                orderStatus = "paid";
            }
            Map<String, Object> result = new HashMap<>();
            result.put("tradeStatus", tradeStatus);
            result.put("orderStatus", orderStatus);
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("查询支付状态失败: " + e.getMessage());
        }
    }

}
