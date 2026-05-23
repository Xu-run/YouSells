package com.yousells.modules.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WeeklyReportCreateRequest(
        @NotBlank(message = "weekKey cannot be blank")
        @Size(max = 8, message = "weekKey must be less than 8 characters")
        String weekKey,

        @NotBlank(message = "summary cannot be blank")
        @Size(max = 2000, message = "summary must be less than 2000 characters")
        String summary,

        @Size(max = 2000, message = "issues must be less than 2000 characters")
        String issues,

        @NotBlank(message = "nextWeekPlan cannot be blank")
        @Size(max = 2000, message = "nextWeekPlan must be less than 2000 characters")
        String nextWeekPlan
) {
}
