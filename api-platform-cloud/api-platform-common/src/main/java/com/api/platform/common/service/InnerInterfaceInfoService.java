package com.api.platform.common.service;

import com.api.platform.common.vo.InterfaceInfoVO;

/**
 * 内部接口信息服务接口（Dubbo） —— 供网关查询接口信息
 *
 * 消费方：网关 InterfaceValidateFilter（校验接口是否存在、是否审核通过）
 * 提供方：后端 InnerInterfaceInfoServiceImpl
 */
public interface InnerInterfaceInfoService {

    InterfaceInfoVO getInterfaceInfo(String path, String method);

    InterfaceInfoVO getInterfaceInfoById(Long id);
}
