package com.yousells.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "realName cannot be blank")
        @Size(max = 50, message = "realName too long")
        String realName
) {
}
