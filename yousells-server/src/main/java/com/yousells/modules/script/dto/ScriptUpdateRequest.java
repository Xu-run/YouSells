package com.yousells.modules.script.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScriptUpdateRequest(
        @NotNull(message = "categoryId cannot be null")
        Long categoryId,
        @NotBlank(message = "title cannot be blank")
        String title,
        @NotBlank(message = "content cannot be blank")
        String content,
        String applicableScene,
        String status
) {
}
