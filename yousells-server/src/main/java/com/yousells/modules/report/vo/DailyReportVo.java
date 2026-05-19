package com.yousells.modules.report.vo;

import java.time.LocalDate;

public record DailyReportVo(
        Long id,
        LocalDate reportDate,
        String userDisplayName,
        String todayWork,
        String issues,
        String tomorrowPlan
) {
}
