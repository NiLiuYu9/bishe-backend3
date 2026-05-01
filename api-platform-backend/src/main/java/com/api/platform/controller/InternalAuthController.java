package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.common.ResultCode;
import com.api.platform.dto.UserInfoDTO;
import com.api.platform.entity.User;
import com.api.platform.service.AccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 内部鉴权控制器 —— 处理网关调用AK/SK校验请求
 *
 * 路由前缀：/internal/auth
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 此控制器仅供 API 网关内部调用，不对外暴露。
 * 网关的 AuthFilter 通过 Dubbo RPC 或 HTTP 调用此接口验证 AK/SK 有效性
 */
@RestController
@RequestMapping("/internal/auth")
public class InternalAuthController {

    @Autowired
    private AccessKeyService accessKeyService;

    /**
     * 校验AK/SK有效性
     *
     * 网关在接收到API调用请求时，通过此接口验证请求头中的 AK/SK 是否合法。
     * 校验通过后返回用户信息，网关据此进行后续的配额检查和路由转发
     *
     * @param accessKey 请求头中的访问密钥
     * @param secretKey 请求头中的秘密密钥
     * @return Result&lt;UserInfoDTO&gt; 校验通过返回用户信息（userId、username、status等）
     */
    @GetMapping("/validate")
    public Result<UserInfoDTO> validateAccessKey(
            @RequestHeader(value = "X-Access-Key", required = false) String accessKey,
            @RequestHeader(value = "X-Secret-Key", required = false) String secretKey) {
        
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            return Result.error(ResultCode.AK_SK_REQUIRED);
        }

        try {
            User user = accessKeyService.validateAccessKey(accessKey, secretKey);
            UserInfoDTO dto = new UserInfoDTO();
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setAccessKey(user.getAccessKey());
            dto.setSecretKey(user.getSecretKey());
            dto.setStatus(user.getStatus());
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(ResultCode.UNAUTHORIZED, e.getMessage());
        }
    }
}
