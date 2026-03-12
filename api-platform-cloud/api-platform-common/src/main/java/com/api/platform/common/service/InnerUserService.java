package com.api.platform.common.service;

import com.api.platform.common.vo.InvokeUserVO;

public interface InnerUserService {

    InvokeUserVO getInvokeUser(String accessKey);
}
