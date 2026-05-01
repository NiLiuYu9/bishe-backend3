package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.entity.User;
import com.api.platform.service.AccessKeyService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.AccessKeyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 密钥管理控制器 —— 处理用户AK/SK获取与重新生成请求
 *
 * 路由前缀：/user/accessKey
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/user/accessKey")
public class AccessKeyController {

    @Autowired
    private AccessKeyService accessKeyService;

    /**
     * 获取当前用户的AK/SK信息
     *
     * 若用户尚无AK/SK，则自动生成后返回
     *
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;AccessKeyVO&gt; 包含用户ID、用户名、accessKey、secretKey
     */
    @GetMapping("/info")
    public Result<AccessKeyVO> getAccessKey(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        User user = accessKeyService.getOrGenerateAccessKey(userId);
        return Result.success(convertToVO(user));
    }

    /**
     * 重新生成AK/SK
     *
     * 重新生成后旧密钥立即失效，需同步更新SDK配置
     *
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;AccessKeyVO&gt; 包含新的 accessKey 和 secretKey
     */
    @PostMapping("/regenerate")
    public Result<AccessKeyVO> regenerateAccessKey(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        accessKeyService.regenerateAccessKey(userId);
        User user = accessKeyService.getById(userId);
        return Result.success(convertToVO(user));
    }

    private AccessKeyVO convertToVO(User user) {
        AccessKeyVO vo = new AccessKeyVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        return vo;
    }

}
