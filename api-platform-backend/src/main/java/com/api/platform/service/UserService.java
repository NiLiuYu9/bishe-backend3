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
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    User getByUsername(String username);

    UserVO getCurrentUser(HttpServletRequest request);

    UserVO updateUserInfo(UpdateUserDTO updateUserDTO, HttpServletRequest request);

    void updatePassword(UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request);

    IPage<UserVO> pageUserList(UserQueryDTO userQueryDTO);

    void freezeUser(Long userId, FreezeUserDTO freezeUserDTO);

    void unfreezeUser(Long userId);

}
