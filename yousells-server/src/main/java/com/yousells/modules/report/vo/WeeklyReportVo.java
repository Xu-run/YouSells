package com.yousells.modules.report.vo;

public record WeeklyReportVo(
        Long id,
        String weekKey,
        String userDisplayName,
        String weeklySummary,
        String issues,
        String nextWeekPlan
) {
}
