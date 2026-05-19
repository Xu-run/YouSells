package com.yousells.modules.auth.vo;

public record LoginResultVo(
        String accessToken,
        String tokenType,
        long expiresIn,
        CurrentUserVo userInfo
) {
}
