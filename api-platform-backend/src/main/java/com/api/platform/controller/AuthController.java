package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.LoginDTO;
import com.api.platform.dto.RegisterDTO;
import com.api.platform.dto.UpdatePasswordDTO;
import com.api.platform.dto.UpdateUserDTO;
import com.api.platform.vo.LoginVO;
import com.api.platform.vo.UserVO;
import com.api.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        LoginVO loginVO = userService.login(loginDTO, request);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return Result.success();
    }

    @GetMapping("/user-info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        UserVO userVO = userService.getCurrentUser(request);
        return Result.success(userVO);
    }

    @PutMapping("/user-info")
    public Result<UserVO> updateUserInfo(@Validated @RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        UserVO userVO = userService.updateUserInfo(updateUserDTO, request);
        return Result.success(userVO);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        userService.updatePassword(updatePasswordDTO, request);
        return Result.success();
    }

}
