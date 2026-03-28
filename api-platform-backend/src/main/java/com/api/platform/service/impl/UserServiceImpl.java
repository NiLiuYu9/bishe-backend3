package com.api.platform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;
import com.api.platform.common.ResultCode;
import com.api.platform.constants.SessionConstants;
import com.api.platform.dto.FreezeUserDTO;
import com.api.platform.dto.LoginDTO;
import com.api.platform.dto.RegisterDTO;
import com.api.platform.dto.UpdatePasswordDTO;
import com.api.platform.dto.UpdateUserDTO;
import com.api.platform.dto.UserQueryDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.User;
import com.api.platform.vo.LoginVO;
import com.api.platform.vo.UserVO;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiCacheService;
import com.api.platform.service.UserTagService;
import com.api.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "api_platform";

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private ApiCacheService apiCacheService;

    @Autowired
    private UserTagService userTagService;

    @Override
    public void register(RegisterDTO registerDTO) {
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        user.setIsAdmin(0);
        user.setAccessKey(DigestUtil.md5Hex(SALT + registerDTO.getUsername() + RandomUtil.randomNumbers(5)));
        user.setSecretKey(DigestUtil.md5Hex(SALT + registerDTO.getUsername() + RandomUtil.randomNumbers(8)));
        save(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionConstants.USER_ID, user.getId());
        session.setAttribute(SessionConstants.USERNAME, user.getUsername());
        session.setAttribute(SessionConstants.USER, user);
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setIsAdmin(user.getIsAdmin());
        return loginVO;
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    private User getCurrentLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserVO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return null;
        }
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setIsAdmin(user.getIsAdmin());
        userVO.setStatus(user.getStatus());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setTags(userTagService.getTagsByUserId(user.getId()));
        return userVO;
    }

    @Override
    public UserVO updateUserInfo(UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        User user = getCurrentLoginUser(request);
        String oldUsername = user.getUsername();
        boolean usernameChanged = false;
        
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(user.getUsername())) {
            User existUser = getByUsername(updateUserDTO.getUsername());
            if (existUser != null) {
                throw new BusinessException(ResultCode.USER_EXISTS);
            }
            user.setUsername(updateUserDTO.getUsername());
            usernameChanged = true;
        }
        if (updateUserDTO.getEmail() != null) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getPhone() != null) {
            user.setPhone(updateUserDTO.getPhone());
        }
        updateById(user);
        
        if (usernameChanged) {
            updateApiCacheUsername(user.getId(), user.getUsername());
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SessionConstants.USER_ID, user.getId());
            session.setAttribute(SessionConstants.USERNAME, user.getUsername());
            session.setAttribute(SessionConstants.USER, user);
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setIsAdmin(user.getIsAdmin());
        userVO.setStatus(user.getStatus());
        userVO.setCreateTime(user.getCreateTime());
        return userVO;
    }

    private void updateApiCacheUsername(Long userId, String newUsername) {
        List<ApiInfo> apiInfoList = apiInfoMapper.selectList(
                new LambdaQueryWrapper<ApiInfo>()
                        .eq(ApiInfo::getUserId, userId)
        );
        
        for (ApiInfo apiInfo : apiInfoList) {
            com.api.platform.vo.ApiVO cachedVO = apiCacheService.getApiDetailFromCache(apiInfo.getId());
            if (cachedVO != null) {
                cachedVO.setUsername(newUsername);
                apiCacheService.cacheApiDetail(apiInfo.getId(), cachedVO);
            }
        }
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        User user = getCurrentLoginUser(request);
        if (!BCrypt.checkpw(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setPassword(BCrypt.hashpw(updatePasswordDTO.getNewPassword()));
        updateById(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SessionConstants.USER, user);
        }
    }

    @Override
    public IPage<UserVO> pageUserList(UserQueryDTO userQueryDTO) {
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(userQueryDTO.getUsername()), User::getUsername, userQueryDTO.getUsername())
                .eq(userQueryDTO.getStatus() != null, User::getStatus, userQueryDTO.getStatus())
                .orderByDesc(User::getCreateTime);
        IPage<User> userPage = page(page, queryWrapper);
        IPage<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        userVOPage.setRecords(userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(java.util.stream.Collectors.toList()));
        return userVOPage;
    }

    @Override
    public void freezeUser(Long userId, FreezeUserDTO freezeUserDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(0);
        user.setFreezeReason(freezeUserDTO.getReason());
        updateById(user);
    }

    @Override
    public void unfreezeUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(1);
        user.setFreezeReason(null);
        updateById(user);
    }

}
