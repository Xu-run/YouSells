package com.yousells.modules.auth.vo;

import java.util.List;

public record CurrentUserVo(
        Long userId,
        String username,
        String displayName,
        List<String> roles
) {
}
