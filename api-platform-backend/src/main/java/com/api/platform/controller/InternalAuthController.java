package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.common.ResultCode;
import com.api.platform.dto.UserInfoDTO;
import com.api.platform.entity.User;
import com.api.platform.service.AccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auth")
public class InternalAuthController {

    @Autowired
    private AccessKeyService accessKeyService;

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
