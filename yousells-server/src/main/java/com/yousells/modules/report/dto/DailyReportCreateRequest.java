package com.yousells.modules.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyReportCreateRequest(
        @NotNull(message = "reportDate cannot be null")
        LocalDate reportDate,
        @NotBlank(message = "todayWork cannot be blank")
        String todayWork,
        String issues,
        @NotBlank(message = "tomorrowPlan cannot be blank")
        String tomorrowPlan
) {
}
