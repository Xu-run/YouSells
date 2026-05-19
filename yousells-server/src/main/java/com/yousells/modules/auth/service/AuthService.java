package com.yousells.modules.auth.service;

import com.yousells.modules.auth.dto.LoginRequest;
import com.yousells.modules.auth.vo.CurrentUserVo;
import com.yousells.modules.auth.vo.LoginResultVo;

public interface AuthService {

    LoginResultVo login(LoginRequest request);

    CurrentUserVo currentUser();

    void logout();
}
