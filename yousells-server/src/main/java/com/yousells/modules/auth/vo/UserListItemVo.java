package com.yousells.modules.auth.vo;

public record UserListItemVo(
        Long userId,
        String username,
        String realName,
        String level,
        Long managerUserId,
        String status
) {
}
