package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.entity.User;
import com.api.platform.service.AccessKeyService;
import com.api.platform.vo.AccessKeyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/accessKey")
public class AccessKeyController {

    @Autowired
    private AccessKeyService accessKeyService;

    @GetMapping("/info")
    public Result<AccessKeyVO> getAccessKey(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.unauthorized();
        }
        User user = accessKeyService.getById(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        if (user.getAccessKey() == null || user.getAccessKey().isEmpty()) {
            accessKeyService.generateAccessKey(userId);
            user = accessKeyService.getById(userId);
        }
        AccessKeyVO vo = new AccessKeyVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        return Result.success(vo);
    }

    @PostMapping("/regenerate")
    public Result<AccessKeyVO> regenerateAccessKey(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.unauthorized();
        }
        accessKeyService.regenerateAccessKey(userId);
        User user = accessKeyService.getById(userId);
        AccessKeyVO vo = new AccessKeyVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        return Result.success(vo);
    }

}
