package com.api.platform.service;

import com.api.platform.dto.FreezeUserDTO;
import com.api.platform.dto.LoginDTO;
import com.api.platform.dto.RegisterDTO;
import com.api.platform.dto.UpdatePasswordDTO;
import com.api.platform.dto.UpdateUserDTO;
import com.api.platform.dto.UserQueryDTO;
import com.api.platform.entity.User;
import com.api.platform.vo.LoginVO;
import com.api.platform.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口 —— 定义用户相关的核心业务操作
 *
 * 所属业务模块：用户管理模块
 * 包括用户注册、登录、信息查询、信息修改、密码修改、用户冻结/解冻等功能
 * 实现类为 UserServiceImpl
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerDTO 注册表单（用户名、密码、确认密码）
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * 登录成功后创建 Session，Session 由 Spring Session + Redis 管理
     *
     * @param loginDTO 登录表单（用户名、密码）
     * @param request  HttpServletRequest，用于创建 Session
     * @return LoginVO 包含用户基本信息和 accessKey
     */
    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    /**
     * 根据用户名查询用户实体
     *
     * @param username 用户名
     * @return User 用户实体，不存在则返回 null
     */
    User getByUsername(String username);

    /**
     * 获取当前登录用户信息
     *
     * 从 Session 中获取当前用户 ID，查询并返回用户详细信息
     *
     * @param request HttpServletRequest，用于从 Session 中获取当前用户
     * @return UserVO 当前登录用户的详细信息
     */
    UserVO getCurrentUser(HttpServletRequest request);

    /**
     * 更新当前登录用户信息
     *
     * @param updateUserDTO 用户信息更新表单（昵称、头像等）
     * @param request       HttpServletRequest，用于获取当前登录用户
     * @return UserVO 更新后的用户信息
     */
    UserVO updateUserInfo(UpdateUserDTO updateUserDTO, HttpServletRequest request);

    /**
     * 修改当前登录用户密码
     *
     * 需验证旧密码是否正确
     *
     * @param updatePasswordDTO 密码修改表单（旧密码、新密码、确认新密码）
     * @param request           HttpServletRequest，用于获取当前登录用户
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request);

    /**
     * 分页查询用户列表（管理端）
     *
     * 支持按用户名、状态等条件筛选
     *
     * @param userQueryDTO 用户查询条件（用户名、状态、分页参数）
     * @return IPage<UserVO> 分页用户信息列表
     */
    IPage<UserVO> pageUserList(UserQueryDTO userQueryDTO);

    /**
     * 冻结用户
     *
     * 冻结后用户无法登录，需提供冻结原因
     *
     * @param userId        待冻结的用户 ID
     * @param freezeUserDTO 冻结信息（冻结原因）
     */
    void freezeUser(Long userId, FreezeUserDTO freezeUserDTO);

    /**
     * 解冻用户
     *
     * 解冻后用户恢复正常登录权限
     *
     * @param userId 待解冻的用户 ID
     */
    void unfreezeUser(Long userId);

}
