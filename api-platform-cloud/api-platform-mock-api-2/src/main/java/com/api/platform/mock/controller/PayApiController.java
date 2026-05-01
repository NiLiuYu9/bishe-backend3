package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
/**
 * 支付服务模拟API —— 模拟支付创建、支付查询等接口
 *
 * 路由前缀：/api/v1/pay
 * 网关通过 targetUrl 将 /invoke/pay 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/pay")
public class PayApiController {

    private static final String[] PAY_STATUSES = {"pending", "paid", "closed", "refunded"};

    @PostMapping("/alipay")
    public Result<AlipayData> alipay(@RequestBody Map<String, Object> params) {
        log.info("PayAPI - alipay request, params: {}", params);
        AlipayData data = new AlipayData();
        data.setPayUrl("https://openapi.alipay.com/gateway.do?method=trade.page.pay&out_trade_no=" + RandomUtil.randomString(16));
        data.setOrderNo(generateOrderNo());
        log.info("PayAPI - alipay result: orderNo={}", data.getOrderNo());
        return Result.success(data);
    }

    @PostMapping("/wechat")
    public Result<WechatPayData> wechat(@RequestBody Map<String, Object> params) {
        log.info("PayAPI - wechat pay request, params: {}", params);
        WechatPayData data = new WechatPayData();
        data.setQrCode("weixin://wxpay/bizpayurl?pr=" + RandomUtil.randomString(16));
        data.setOrderNo(generateOrderNo());
        log.info("PayAPI - wechat pay result: orderNo={}", data.getOrderNo());
        return Result.success(data);
    }

    @PostMapping("/unionpay")
    public Result<UnionPayData> unionpay(@RequestBody Map<String, Object> params) {
        log.info("PayAPI - unionpay request, params: {}", params);
        UnionPayData data = new UnionPayData();
        data.setPayUrl("https://gateway.95516.com/gateway/api/frontTransReq.do?orderId=" + RandomUtil.randomString(12));
        data.setOrderNo(generateOrderNo());
        log.info("PayAPI - unionpay result: orderNo={}", data.getOrderNo());
        return Result.success(data);
    }

    @GetMapping("/query")
    public Result<PayQueryData> query(@RequestParam Map<String, String> params) {
        log.info("PayAPI - query request, params: {}", params);
        PayQueryData data = new PayQueryData();
        data.setStatus(RandomUtil.randomEle(PAY_STATUSES));
        data.setAmount(Math.round(RandomUtil.randomDouble(0.01, 9999.99) * 100.0) / 100.0);
        data.setPayTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("PayAPI - query result: status={}, amount={}", data.getStatus(), data.getAmount());
        return Result.success(data);
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + RandomUtil.randomNumbers(6);
    }

    @Data
    public static class AlipayData {
        private String payUrl;
        private String orderNo;
    }

    @Data
    public static class WechatPayData {
        private String qrCode;
        private String orderNo;
    }

    @Data
    public static class UnionPayData {
        private String payUrl;
        private String orderNo;
    }

    @Data
    public static class PayQueryData {
        private String status;
        private Double amount;
        private String payTime;
    }
}
