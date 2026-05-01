package com.api.platform.utils;

import com.api.platform.constants.SessionConstants;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.common.ResultCode;

import javax.servlet.http.HttpSession;

/**
 * Session工具类
 * <p>核心职责：从HttpSession中获取当前登录用户信息（用户ID、用户名、用户对象），
 * 统一封装Session访问逻辑，避免Controller中直接操作Session。
 * 未登录时抛出BusinessException（UNAUTHORIZED）。</p>
 */
public final class SessionUtils {

    private SessionUtils() {
    }

    /**
     * 获取当前登录用户ID
     * <p>从Session中取userId，未登录时抛出401异常。</p>
     *
     * @param session HTTP会话
     * @return 当前登录用户ID
     * @throws BusinessException 未登录时抛出UNAUTHORIZED异常
     */
    public static Long getCurrentUserId(HttpSession session) {
        if (session == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * 获取当前登录用户名
     * <p>从Session中取username，未登录时抛出401异常。</p>
     *
     * @param session HTTP会话
     * @return 当前登录用户名
     * @throws BusinessException 未登录时抛出UNAUTHORIZED异常
     */
    public static String getCurrentUsername(HttpSession session) {
        if (session == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String username = (String) session.getAttribute(SessionConstants.USERNAME);
        if (username == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return username;
    }

    /**
     * 获取当前登录用户完整对象
     * <p>从Session中取User对象，未登录时抛出401异常。</p>
     *
     * @param session HTTP会话
     * @return 当前登录用户实体
     * @throws BusinessException 未登录时抛出UNAUTHORIZED异常
     */
    public static User getCurrentUser(HttpSession session) {
        if (session == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = (User) session.getAttribute(SessionConstants.USER);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    /**
     * 获取当前登录用户ID（允许未登录）
     * <p>与getCurrentUserId不同，未登录时返回null而非抛异常，
     * 适用于可选登录的场景。</p>
     *
     * @param session HTTP会话
     * @return 用户ID，未登录时返回null
     */
    public static Long getCurrentUserIdOrNull(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SessionConstants.USER_ID);
    }

    /**
     * 判断用户是否已登录
     *
     * @param session HTTP会话
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SessionConstants.USER_ID) != null;
    }

    /**
     * 判断当前用户是否为管理员
     * <p>通过Session中的User对象判断isAdmin字段是否为1。</p>
     *
     * @param session HTTP会话
     * @return true-管理员，false-非管理员
     */
    public static boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute(SessionConstants.USER);
        return user != null && user.getIsAdmin() != null && user.getIsAdmin() == 1;
    }
}
