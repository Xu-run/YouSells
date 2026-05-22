package com.yousells.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = "oldPassword cannot be blank")
        String oldPassword,

        @NotBlank(message = "newPassword cannot be blank")
        @Size(min = 6, max = 50, message = "newPassword must be 6-50 characters")
        String newPassword
) {
}
