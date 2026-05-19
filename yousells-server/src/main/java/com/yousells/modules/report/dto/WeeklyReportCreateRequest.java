package com.yousells.modules.report.dto;

import jakarta.validation.constraints.NotBlank;

public record WeeklyReportCreateRequest(
        @NotBlank(message = "weekKey cannot be blank")
        String weekKey,
        @NotBlank(message = "weeklySummary cannot be blank")
        String weeklySummary,
        String issues,
        @NotBlank(message = "nextWeekPlan cannot be blank")
        String nextWeekPlan
) {
}
