package com.yousells.modules.auth.vo;

public record UserProfileVo(
        Long userId,
        String username,
        String realName,
        String level,
        Long managerUserId,
        String status
) {
}
