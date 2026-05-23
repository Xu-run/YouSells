package com.yousells.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResignUserRequest(
        @NotBlank(message = "离职原因不能为空")
        @Size(max = 500, message = "离职原因最多 500 字")
        String reason
) {
}
