package com.yousells.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "realName cannot be blank")
        @Size(max = 50, message = "realName too long")
        String realName,

        @NotBlank(message = "level cannot be blank")
        @Pattern(regexp = "T[0-3]", message = "level must be T0, T1, T2 or T3")
        String level,

        Long managerUserId,

        @Pattern(regexp = "ACTIVE|DISABLED", message = "status must be ACTIVE or DISABLED")
        String status
) {
}
