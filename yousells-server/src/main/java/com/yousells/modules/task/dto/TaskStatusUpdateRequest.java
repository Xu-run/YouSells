package com.yousells.modules.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TaskStatusUpdateRequest(
        @NotBlank(message = "status cannot be blank")
        @Pattern(regexp = "待开始|进行中|已完成", message = "status must be one of: 待开始, 进行中, 已完成")
        String status
) {
}
