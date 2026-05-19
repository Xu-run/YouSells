package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerCreateRequest(
        @NotBlank(message = "nickname cannot be blank")
        String nickname,
        @NotBlank(message = "contactValue cannot be blank")
        String contactValue,
        @NotBlank(message = "sourcePlatform cannot be blank")
        String sourcePlatform,
        String customerType,
        String expectedMajor,
        String baseLevel,
        String intentLevel,
        String currentStage,
        @NotNull(message = "ownerUserId cannot be null")
        Long ownerUserId,
        Long assistantUserId,
        String remarks
) {
}
