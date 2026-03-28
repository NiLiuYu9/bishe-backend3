package com.api.platform.service;

import java.util.Map;

public interface AlipayService {
    String createPayment(Long orderId, String orderNo, String subject, String totalAmount) throws Exception;
    
    boolean verifyAndProcessNotify(Map<String, String> params);
    
    String queryPaymentStatus(String orderNo) throws Exception;
}
