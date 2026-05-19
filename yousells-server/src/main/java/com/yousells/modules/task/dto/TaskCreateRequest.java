package com.yousells.modules.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskCreateRequest(
        @NotBlank(message = "taskTitle cannot be blank")
        String taskTitle,
        String taskType,
        String taskDescription,
        String priority,
        @NotNull(message = "ownerUserId cannot be null")
        Long ownerUserId,
        Long assistantUserId,
        LocalDateTime dueAt
) {
}
