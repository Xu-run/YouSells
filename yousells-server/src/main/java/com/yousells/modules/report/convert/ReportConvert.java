package com.yousells.modules.report.convert;

import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.entity.DailyReportEntity;
import com.yousells.modules.report.entity.WeeklyReportEntity;

public final class ReportConvert {

    private ReportConvert() {
    }

    public static DailyReportEntity toDailyEntity(DailyReportCreateRequest request, Long userId) {
        DailyReportEntity entity = new DailyReportEntity();
        entity.setReportDate(request.reportDate());
        entity.setUserId(userId);
        entity.setTodayWork(request.todayWork());
        entity.setIssues(request.issues());
        entity.setTomorrowPlan(request.tomorrowPlan());
        return entity;
    }

    public static WeeklyReportEntity toWeeklyEntity(WeeklyReportCreateRequest request, Long userId) {
        WeeklyReportEntity entity = new WeeklyReportEntity();
        entity.setWeekKey(request.weekKey());
        entity.setUserId(userId);
        entity.setWeeklySummary(request.weeklySummary());
        entity.setIssues(request.issues());
        entity.setNextWeekPlan(request.nextWeekPlan());
        return entity;
    }
}
