package com.api.platform.service;

import java.util.Map;

/**
 * 支付宝支付服务接口 —— 定义支付宝支付相关的业务操作
 *
 * 所属业务模块：支付模块
 * 包括创建支付、回调处理、支付状态查询等功能
 * 实现类为 AlipayServiceImpl
 */
public interface AlipayService {

    /**
     * 创建支付宝支付订单
     *
     * 调用支付宝沙箱环境创建支付，返回支付页面表单
     *
     * @param orderId     系统内部订单 ID
     * @param orderNo     系统内部订单编号
     * @param subject     支付主题（商品名称）
     * @param totalAmount 支付金额
     * @return String 支付宝支付页面的 HTML 表单
     * @throws Exception 支付宝接口调用异常
     */
    String createPayment(Long orderId, String orderNo, String subject, String totalAmount) throws Exception;

    /**
     * 验证并处理支付宝异步回调通知
     *
     * 验证回调签名后更新订单状态为已支付
     *
     * @param params 支付宝回调参数（trade_no、out_trade_no、trade_status 等）
     * @return boolean 处理成功返回 true，验证失败返回 false
     */
    boolean verifyAndProcessNotify(Map<String, String> params);

    /**
     * 查询支付宝支付状态
     *
     * 主动向支付宝查询指定订单的支付状态
     *
     * @param orderNo 系统内部订单编号
     * @return String 支付状态（TRADE_SUCCESS、WAIT_BUYER_PAY 等）
     * @throws Exception 支付宝接口调用异常
     */
    String queryPaymentStatus(String orderNo) throws Exception;

}
