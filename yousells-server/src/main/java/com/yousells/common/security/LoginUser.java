package com.yousells.common.security;

import java.io.Serializable;
import java.util.List;

public record LoginUser(
        Long userId,
        String username,
        String displayName,
        List<String> roles
) implements Serializable {
}
