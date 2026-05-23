package com.yousells.modules.report.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DailyReportCreateRequest(
        @NotNull(message = "reportDate cannot be null")
        LocalDate reportDate,

        @NotNull(message = "summary cannot be null")
        @Size(max = 2000, message = "summary must be less than 2000 characters")
        String summary,

        @Size(max = 2000, message = "issues must be less than 2000 characters")
        String issues,

        @NotNull(message = "tomorrowPlan cannot be null")
        @Size(max = 2000, message = "tomorrowPlan must be less than 2000 characters")
        String tomorrowPlan
) {
}
