package com.yousells.modules.auth.service.impl;

import com.yousells.common.constant.AuthConstants;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.security.LoginUser;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.auth.dto.LoginRequest;
import com.yousells.modules.auth.service.AuthService;
import com.yousells.modules.auth.vo.CurrentUserVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final Map<String, SeedUser> seedUsers;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.seedUsers = Map.of(
                "admin", new SeedUser(1L, "admin", "系统管理员", passwordEncoder.encode("admin123"), List.of("ADMIN")),
                "member", new SeedUser(2L, "member", "普通成员", passwordEncoder.encode("member123"), List.of("MEMBER"))
        );
    }

    @Override
    public CurrentUserVo login(LoginRequest request, HttpSession session) {
        SeedUser seedUser = seedUsers.get(request.username());
        if (seedUser == null || !passwordEncoder.matches(request.password(), seedUser.passwordHash())) {
            throw new BusinessException(ErrorCodeConstants.UNAUTHORIZED, "invalid username or password");
        }
        LoginUser loginUser = new LoginUser(
                seedUser.userId(),
                seedUser.username(),
                seedUser.displayName(),
                seedUser.roles()
        );
        session.setAttribute(AuthConstants.SESSION_LOGIN_USER, loginUser);
        return toVo(loginUser);
    }

    @Override
    public CurrentUserVo currentUser() {
        return toVo(SecurityUserContext.requireCurrentUser());
    }

    @Override
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    private CurrentUserVo toVo(LoginUser loginUser) {
        return new CurrentUserVo(
                loginUser.userId(),
                loginUser.username(),
                loginUser.displayName(),
                loginUser.roles()
        );
    }

    private record SeedUser(
            Long userId,
            String username,
            String displayName,
            String passwordHash,
            List<String> roles
    ) {
    }
}
