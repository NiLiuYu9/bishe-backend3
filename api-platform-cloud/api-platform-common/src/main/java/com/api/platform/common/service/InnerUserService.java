package com.api.platform.common.service;

import com.api.platform.common.vo.InvokeUserVO;

/**
 * 内部用户服务接口（Dubbo） —— 供网关按accessKey查询用户信息
 *
 * 消费方：网关 AuthFilter（鉴权时查询用户secretKey用于签名校验）
 * 提供方：后端 InnerUserServiceImpl
 */
public interface InnerUserService {

    InvokeUserVO getInvokeUser(String accessKey);
}
