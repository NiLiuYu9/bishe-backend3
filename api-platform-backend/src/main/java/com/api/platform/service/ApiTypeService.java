package com.api.platform.service;

import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.entity.ApiType;
import com.api.platform.vo.ApiTypeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ApiTypeService extends IService<ApiType> {

    List<ApiType> getAllTypes();

    IPage<ApiTypeVO> pageApiTypes(ApiTypeQueryDTO queryDTO);

    void createType(ApiType apiType);

    void updateType(ApiType apiType);

    void updateStatus(Long id, String status);

}
