package com.api.platform.service.dubbo;

import com.api.platform.common.service.InnerUserService;
import com.api.platform.common.vo.InvokeUserVO;
import com.api.platform.entity.User;
import com.api.platform.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * Dubbo服务实现 - 内部用户服务
 * <p>核心职责：按accessKey查询用户信息，供网关鉴权调用。
 * 网关在接收到API请求时，通过AK/SK签名验证调用者身份，
 * 本服务提供根据accessKey查找对应用户的能力。</p>
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据accessKey查询可调用用户信息
     * <p>网关鉴权流程中调用此方法，通过请求携带的accessKey定位用户，
     * 返回包含secretKey的VO对象，用于后续签名验证。</p>
     *
     * @param accessKey 用户的访问密钥标识
     * @return 包含用户ID、AK、SK和状态的调用者信息VO；accessKey为空或用户不存在时返回null
     */
    @Override
    public InvokeUserVO getInvokeUser(String accessKey) {
        if (accessKey == null || accessKey.isEmpty()) {
            return null;
        }
        // 根据accessKey精确匹配用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccessKey, accessKey));
        if (user == null) {
            return null;
        }
        // 组装网关鉴权所需的用户信息（包含secretKey用于签名校验）
        InvokeUserVO vo = new InvokeUserVO();
        vo.setId(user.getId());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
