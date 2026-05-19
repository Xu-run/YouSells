package com.yousells.modules.auth.service;

import com.yousells.modules.auth.dto.LoginRequest;
import com.yousells.modules.auth.vo.CurrentUserVo;
import jakarta.servlet.http.HttpSession;

public interface AuthService {

    CurrentUserVo login(LoginRequest request, HttpSession session);

    CurrentUserVo currentUser();

    void logout(HttpSession session);
}
