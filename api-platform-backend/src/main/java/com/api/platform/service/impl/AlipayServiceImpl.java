package com.api.platform.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.api.platform.config.AlipayConfig;
import com.api.platform.entity.OrderInfo;
import com.api.platform.service.AlipayService;
import com.api.platform.service.OrderInfoService;
import com.api.platform.service.UserApiQuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    private static final Logger log = LoggerFactory.getLogger(AlipayServiceImpl.class);

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    private AlipayClient alipayClient;

    @PostConstruct
    public void init() {
        try {
            alipayClient = new DefaultAlipayClient(
                    alipayConfig.getServerUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    alipayConfig.getFormat(),
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType()
            );
            ((DefaultAlipayClient) alipayClient).setConnectTimeout(30000);
            ((DefaultAlipayClient) alipayClient).setReadTimeout(60000);
            log.info("支付宝客户端初始化成功, appId={}", alipayConfig.getAppId());
        } catch (Exception e) {
            log.error("支付宝客户端初始化失败", e);
        }
    }

    @Override
    public String createPayment(Long orderId, String orderNo, String subject, String totalAmount) throws Exception {
        log.info("开始创建支付订单: orderId={}, orderNo={}, subject={}, amount={}", orderId, orderNo, subject, totalAmount);
        
        if (alipayClient == null) {
            log.error("支付宝客户端未初始化");
            throw new RuntimeException("支付宝客户端未初始化，请检查配置");
        }
        
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        
        String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                orderNo, totalAmount, subject
        );
        request.setBizContent(bizContent);
        
        log.info("支付宝请求配置: returnUrl={}, notifyUrl={}", alipayConfig.getReturnUrl(), alipayConfig.getNotifyUrl());
        
        String form = alipayClient.pageExecute(request).getBody();
        log.info("支付表单生成完成, 长度: {}", form != null ? form.length() : 0);
        
        return form;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyAndProcessNotify(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
            
            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return false;
            }
            
            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");
            
            log.info("支付宝回调: orderNo={}, tradeNo={}, status={}, amount={}", 
                    outTradeNo, tradeNo, tradeStatus, totalAmount);
            
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.info("交易状态非成功: {}", tradeStatus);
                return true;
            }
            
            OrderInfo orderInfo = orderInfoService.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, outTradeNo)
                    .one();
            
            if (orderInfo == null) {
                log.error("订单不存在: {}", outTradeNo);
                return false;
            }
            
            if (!"pending".equals(orderInfo.getStatus())) {
                log.info("订单状态非待支付: {}", orderInfo.getStatus());
                return true;
            }
            
            BigDecimal orderAmount = orderInfo.getPrice();
            BigDecimal paidAmount = new BigDecimal(totalAmount);
            if (orderAmount.compareTo(paidAmount) != 0) {
                log.error("订单金额不一致: orderAmount={}, paidAmount={}", orderAmount, paidAmount);
                return false;
            }
            
            orderInfo.setStatus("paid");
            orderInfo.setPayTradeNo(tradeNo);
            orderInfo.setPayMethod("alipay");
            orderInfo.setPayTime(LocalDateTime.now());
            orderInfoService.updateById(orderInfo);
            
            userApiQuotaService.addQuota(orderInfo.getBuyerId(), orderInfo.getApiId(), orderInfo.getInvokeCount());
            
            log.info("订单支付成功: orderId={}", orderInfo.getId());
            return true;
            
        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return false;
        }
    }

    @Override
    public String queryPaymentStatus(String orderNo) throws Exception {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        String bizContent = String.format("{\"out_trade_no\":\"%s\"}", orderNo);
        request.setBizContent(bizContent);
        
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            return response.getTradeStatus();
        }
        return null;
    }
}
