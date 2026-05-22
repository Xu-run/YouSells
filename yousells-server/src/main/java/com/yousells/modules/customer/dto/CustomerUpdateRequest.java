package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerUpdateRequest(
        @NotBlank(message = "realName cannot be blank") String realName,
        @NotBlank(message = "grade cannot be blank") String grade,
        @NotBlank(message = "major cannot be blank") String major,
        String className,
        @NotNull(message = "inviterUserId cannot be null") Long inviterUserId,
        @NotNull(message = "ownerUserId cannot be null") Long ownerUserId,
        String progress,
        String intent,
        String inviterNote
) {}
