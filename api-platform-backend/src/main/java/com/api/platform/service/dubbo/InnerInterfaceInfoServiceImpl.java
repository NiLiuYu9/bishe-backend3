package com.api.platform.service.dubbo;

import com.api.platform.common.service.InnerInterfaceInfoService;
import com.api.platform.common.vo.InterfaceInfoVO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.mapper.ApiInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Override
    public InterfaceInfoVO getInterfaceInfo(String path, String method) {
        if (path == null || method == null) {
            return null;
        }
        ApiInfo apiInfo = apiInfoMapper.selectOne(new LambdaQueryWrapper<ApiInfo>()
                .eq(ApiInfo::getEndpoint, path)
                .eq(ApiInfo::getMethod, method));
        return convertToVO(apiInfo);
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoById(Long id) {
        if (id == null) {
            return null;
        }
        ApiInfo apiInfo = apiInfoMapper.selectById(id);
        return convertToVO(apiInfo);
    }

    private InterfaceInfoVO convertToVO(ApiInfo apiInfo) {
        if (apiInfo == null) {
            return null;
        }
        InterfaceInfoVO vo = new InterfaceInfoVO();
        vo.setId(apiInfo.getId());
        vo.setPath(apiInfo.getEndpoint());
        vo.setMethod(apiInfo.getMethod());
        vo.setStatus(apiInfo.getStatus());
        return vo;
    }
}
