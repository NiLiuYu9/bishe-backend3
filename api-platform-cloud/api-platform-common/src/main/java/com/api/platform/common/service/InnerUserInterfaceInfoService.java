package com.api.platform.common.service;

public interface InnerUserInterfaceInfoService {

    void invokeCount(Long interfaceInfoId, Long userId);

    boolean hasQuota(Long userId, Long interfaceInfoId);
}
