package com.api.platform.utils;

import com.api.platform.constants.SessionConstants;
import com.api.platform.exception.BusinessException;
import com.api.platform.common.ResultCode;

import javax.servlet.http.HttpSession;

public final class SessionUtils {

    private SessionUtils() {
    }

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

    public static Long getCurrentUserIdOrNull(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SessionConstants.USER_ID);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SessionConstants.USER_ID) != null;
    }
}
