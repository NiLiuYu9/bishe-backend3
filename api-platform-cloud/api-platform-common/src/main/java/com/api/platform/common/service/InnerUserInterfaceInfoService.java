package com.api.platform.common.service;

/**
 * 内部用户接口关系服务接口（Dubbo） —— 供网关更新调用次数和检查配额
 *
 * 消费方：网关 InterfaceValidateFilter（检查配额）、ResponseLogFilter（更新调用次数）
 * 提供方：后端 InnerUserInterfaceInfoServiceImpl
 */
public interface InnerUserInterfaceInfoService {

    void invokeCount(Long interfaceInfoId, Long userId);

    boolean hasQuota(Long userId, Long interfaceInfoId);
}
