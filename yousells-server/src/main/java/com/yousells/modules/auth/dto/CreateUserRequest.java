package com.yousells.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "username cannot be blank")
        @Size(min = 3, max = 50, message = "username must be 3-50 characters")
        String username,

        @NotBlank(message = "realName cannot be blank")
        @Size(max = 50, message = "realName too long")
        String realName,

        @NotBlank(message = "password cannot be blank")
        @Size(min = 6, max = 50, message = "password must be 6-50 characters")
        String password,

        @NotBlank(message = "level cannot be blank")
        @Pattern(regexp = "T[0-3]", message = "level must be T0, T1, T2 or T3")
        String level,

        Long managerUserId
) {
}
