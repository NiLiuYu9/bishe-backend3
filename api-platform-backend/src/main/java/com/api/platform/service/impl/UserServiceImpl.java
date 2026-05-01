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

/**
 * 用户服务实现 —— 处理用户注册、登录、Session管理、信息修改、密码更新等核心业务逻辑
 *
 * 核心业务流程：
 * 1. 注册：校验用户名唯一 → BCrypt加密密码 → 生成AK/SK → 保存用户
 * 2. 登录：校验用户存在 → BCrypt校验密码 → 校验账号状态 → 写入Session
 * 3. 改密：校验旧密码 → BCrypt加密新密码 → 更新Session
 * 4. 冻结/解冻：管理员操作，修改用户状态（0=禁用，1=正常）
 *
 * AK/SK生成算法：MD5(SALT + username + 随机数)
 * 密码加密算法：BCrypt
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "api_platform";

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private ApiCacheService apiCacheService;

    @Autowired
    private UserTagService userTagService;

    /**
     * 用户注册
     *
     * 业务流程：
     * 1. 校验用户名是否已存在（唯一性约束）
     * 2. BCrypt加密密码（自动加盐，安全性高于MD5）
     * 3. 生成AK/SK（MD5哈希，用于后续API调用鉴权）
     * 4. 保存用户，初始状态为1（正常），非管理员
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword())); // BCrypt加密，自动生成盐值
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1); // 初始状态：1=正常
        user.setIsAdmin(0); // 非管理员
        user.setAccessKey(DigestUtil.md5Hex(SALT + registerDTO.getUsername() + RandomUtil.randomNumbers(5))); // 生成AccessKey
        user.setSecretKey(DigestUtil.md5Hex(SALT + registerDTO.getUsername() + RandomUtil.randomNumbers(8))); // 生成SecretKey
        save(user);
    }

    /**
     * 用户登录
     *
     * 业务流程：
     * 1. 根据用户名查询用户
     * 2. BCrypt校验密码
     * 3. 校验账号状态（0=禁用则拒绝登录）
     * 4. 将用户信息写入HttpSession
     * 5. 返回登录信息（含userId、username、isAdmin标识）
     */
    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) { // BCrypt密码校验
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) { // 账号被禁用
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionConstants.USER_ID, user.getId()); // 写入Session
        session.setAttribute(SessionConstants.USERNAME, user.getUsername());
        session.setAttribute(SessionConstants.USER, user);
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setIsAdmin(user.getIsAdmin());
        return loginVO;
    }

    /** 根据用户名查询用户 */
    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    /** 从Session中获取当前登录用户，未登录则抛出401异常 */
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

    /**
     * 获取当前登录用户信息
     *
     * 业务流程：
     * 1. 从Session获取userId
     * 2. 查询用户实体
     * 3. 转换为UserVO（含用户标签信息）
     */
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

    /**
     * 修改用户信息
     *
     * 业务流程：
     * 1. 获取当前登录用户
     * 2. 如修改用户名，校验新用户名唯一性
     * 3. 更新用户信息到数据库
     * 4. 如用户名变更，同步更新API缓存中的用户名
     * 5. 刷新Session中的用户信息
     */
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
            usernameChanged = true; // 标记用户名已变更
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

    /** 用户名变更时，同步更新该用户所有API缓存中的用户名 */
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

    /**
     * 修改密码
     *
     * 业务流程：
     * 1. 校验旧密码是否正确
     * 2. BCrypt加密新密码并更新
     * 3. 刷新Session中的用户信息
     */
    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        User user = getCurrentLoginUser(request);
        if (!BCrypt.checkpw(updatePasswordDTO.getOldPassword(), user.getPassword())) { // 校验旧密码
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setPassword(BCrypt.hashpw(updatePasswordDTO.getNewPassword())); // BCrypt加密新密码
        updateById(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SessionConstants.USER, user);
        }
    }

    /** 分页查询用户列表（支持按用户名模糊搜索、按状态筛选） */
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

    /**
     * 冻结用户
     * 将用户状态设为0（禁用），并记录冻结原因
     */
    @Override
    public void freezeUser(Long userId, FreezeUserDTO freezeUserDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(0); // 状态设为0=禁用
        user.setFreezeReason(freezeUserDTO.getReason());
        updateById(user);
    }

    /**
     * 解冻用户
     * 将用户状态恢复为1（正常），并清除冻结原因
     */
    @Override
    public void unfreezeUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(1); // 状态恢复为1=正常
        user.setFreezeReason(null); // 清除冻结原因
        updateById(user);
    }

}
