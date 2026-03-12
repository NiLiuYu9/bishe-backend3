package com.api.platform.service.dubbo;

import com.api.platform.common.service.InnerUserService;
import com.api.platform.common.vo.InvokeUserVO;
import com.api.platform.entity.User;
import com.api.platform.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public InvokeUserVO getInvokeUser(String accessKey) {
        if (accessKey == null || accessKey.isEmpty()) {
            return null;
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccessKey, accessKey));
        if (user == null) {
            return null;
        }
        InvokeUserVO vo = new InvokeUserVO();
        vo.setId(user.getId());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
