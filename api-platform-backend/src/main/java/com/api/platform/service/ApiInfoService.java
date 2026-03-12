package com.api.platform.service;

import com.api.platform.dto.ApiCreateDTO;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiStatusDTO;
import com.api.platform.dto.AuditApiDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ApiInfoService extends IService<ApiInfo> {

    IPage<ApiVO> getApis(ApiQueryDTO queryDTO);

    ApiVO getApiDetailById(Long id);

    ApiVO createApi(Long userId, ApiCreateDTO createDTO);

    ApiVO updateApi(Long userId, Long apiId, ApiCreateDTO updateDTO);

    void updateApiStatus(Long userId, Long apiId, ApiStatusDTO statusDTO);

    void auditApi(Long apiId, AuditApiDTO auditApiDTO);

}
