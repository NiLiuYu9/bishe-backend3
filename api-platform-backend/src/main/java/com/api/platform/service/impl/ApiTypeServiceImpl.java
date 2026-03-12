package com.api.platform.service.impl;

import com.api.platform.common.ResultCode;
import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.entity.ApiType;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiTypeMapper;
import com.api.platform.service.ApiTypeService;
import com.api.platform.vo.ApiTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiTypeServiceImpl extends ServiceImpl<ApiTypeMapper, ApiType> implements ApiTypeService {

    @Override
    public List<ApiType> getAllTypes() {
        return list(new LambdaQueryWrapper<ApiType>()
                .eq(ApiType::getDeleted, 0)
                .orderByAsc(ApiType::getCreateTime));
    }

    @Override
    public IPage<ApiTypeVO> pageApiTypes(ApiTypeQueryDTO queryDTO) {
        Page<ApiType> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        IPage<ApiType> apiTypePage = baseMapper.selectPageIgnoreLogicDelete(page, queryDTO.getStatus(), queryDTO.getKeyword());
        
        IPage<ApiTypeVO> apiTypeVOPage = new Page<>(apiTypePage.getCurrent(), apiTypePage.getSize(), apiTypePage.getTotal());
        List<ApiTypeVO> apiTypeVOList = apiTypePage.getRecords().stream().map(apiType -> {
            ApiTypeVO apiTypeVO = new ApiTypeVO();
            BeanUtils.copyProperties(apiType, apiTypeVO);
            apiTypeVO.setStatus(apiType.getDeleted() == 0 ? "active" : "inactive");
            int apiCount = baseMapper.countApisByTypeId(apiType.getId());
            apiTypeVO.setApiCount(apiCount);
            return apiTypeVO;
        }).collect(Collectors.toList());
        apiTypeVOPage.setRecords(apiTypeVOList);
        return apiTypeVOPage;
    }

    @Override
    public void createType(ApiType apiType) {
        ApiType existType = getOne(new LambdaQueryWrapper<ApiType>()
                .eq(ApiType::getName, apiType.getName()));
        if (existType != null) {
            throw new BusinessException(ResultCode.API_TYPE_EXISTS);
        }
        apiType.setDeleted(0);
        save(apiType);
    }

    @Override
    public void updateType(ApiType apiType) {
        ApiType existType = getOne(new LambdaQueryWrapper<ApiType>()
                .eq(ApiType::getName, apiType.getName())
                .ne(ApiType::getId, apiType.getId()));
        if (existType != null) {
            throw new BusinessException(ResultCode.API_TYPE_EXISTS);
        }
        updateById(apiType);
    }

    @Override
    public void updateStatus(Long id, String status) {
        ApiType apiType = baseMapper.selectByIdIgnoreLogicDelete(id);
        if (apiType == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new RuntimeException("状态无效");
        }
        baseMapper.updateDeletedById(id, "inactive".equals(status) ? 1 : 0);
    }

}
