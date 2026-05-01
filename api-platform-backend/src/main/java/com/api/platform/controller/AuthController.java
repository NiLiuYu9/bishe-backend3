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

/**
 * 认证控制器 —— 处理用户注册、登录、登出、个人信息与密码修改请求
 *
 * 路由前缀：/auth
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * 注册成功后自动生成 accessKey/secretKey，用户可直接登录
     *
     * @param registerDTO 注册表单（用户名、密码、邮箱、手机号）
     * @return Result&lt;Void&gt; 注册成功无返回数据
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 用户登录
     *
     * 登录成功后，Spring Session 会自动创建会话并存入 Redis，
     * 后续请求通过 Cookie 中的 SESSION ID 识别用户身份
     *
     * @param loginDTO 登录表单（用户名、密码）
     * @param request  HttpServletRequest，用于获取 Session
     * @return Result&lt;LoginVO&gt; 包含用户基本信息和 accessKey
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        LoginVO loginVO = userService.login(loginDTO, request);
        return Result.success(loginVO);
    }

    /**
     * 用户登出
     *
     * 使当前 Session 失效，清除 Redis 中的会话信息
     *
     * @param request HttpServletRequest，用于获取并销毁 Session
     * @return Result&lt;Void&gt; 登出成功无返回数据
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     *
     * 通过 Session 中的用户ID查询用户详情
     *
     * @param request HttpServletRequest，用于获取 Session 中的用户ID
     * @return Result&lt;UserVO&gt; 当前用户详细信息
     */
    @GetMapping("/user-info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        UserVO userVO = userService.getCurrentUser(request);
        return Result.success(userVO);
    }

    /**
     * 更新当前登录用户信息
     *
     * 支持修改邮箱、手机号等非敏感字段
     *
     * @param updateUserDTO 更新表单（邮箱、手机号等）
     * @param request       HttpServletRequest，用于获取 Session 中的用户ID
     * @return Result&lt;UserVO&gt; 更新后的用户信息
     */
    @PutMapping("/user-info")
    public Result<UserVO> updateUserInfo(@Validated @RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        UserVO userVO = userService.updateUserInfo(updateUserDTO, request);
        return Result.success(userVO);
    }

    /**
     * 修改密码
     *
     * 需验证旧密码，新密码不能与旧密码相同
     *
     * @param updatePasswordDTO 修改密码表单（旧密码、新密码）
     * @param request           HttpServletRequest，用于获取 Session 中的用户ID
     * @return Result&lt;Void&gt; 修改成功无返回数据
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        userService.updatePassword(updatePasswordDTO, request);
        return Result.success();
    }

}
