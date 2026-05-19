package com.yousells.modules.report.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.vo.DailyReportVo;
import com.yousells.modules.report.vo.WeeklyReportVo;

public interface ReportService {

    PageResponse<DailyReportVo> pageDailyReports(int page, int pageSize);

    Long createDailyReport(DailyReportCreateRequest request);

    PageResponse<WeeklyReportVo> pageWeeklyReports(int page, int pageSize);

    Long createWeeklyReport(WeeklyReportCreateRequest request);
}
