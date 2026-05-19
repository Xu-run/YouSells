package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CustomerNextFollowRequest(
        @NotBlank(message = "nextAction cannot be blank")
        String nextAction,
        @NotNull(message = "nextFollowAt cannot be null")
        LocalDateTime nextFollowAt
) {
}
