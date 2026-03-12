package com.api.platform.common.service;

import com.api.platform.common.vo.InterfaceInfoVO;

public interface InnerInterfaceInfoService {

    InterfaceInfoVO getInterfaceInfo(String path, String method);

    InterfaceInfoVO getInterfaceInfoById(Long id);
}
