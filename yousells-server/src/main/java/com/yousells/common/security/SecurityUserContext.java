package com.yousells.common.security;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUserContext {

    private SecurityUserContext() {
    }

    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    public static LoginUser requireCurrentUser() {
        LoginUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCodeConstants.UNAUTHORIZED, "login required");
        }
        return currentUser;
    }
}
