package com.yousells.modules.report.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.IdResponse;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.service.ReportService;
import com.yousells.modules.report.vo.DailyReportVo;
import com.yousells.modules.report.vo.WeeklyReportVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public ApiResponse<PageResponse<DailyReportVo>> daily(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(reportService.pageDailyReports(page, pageSize));
    }

    @PostMapping("/daily")
    public ApiResponse<IdResponse> createDaily(@Valid @RequestBody DailyReportCreateRequest request) {
        return ApiResponse.success(new IdResponse(reportService.createDailyReport(request)));
    }

    @GetMapping("/weekly")
    public ApiResponse<PageResponse<WeeklyReportVo>> weekly(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(reportService.pageWeeklyReports(page, pageSize));
    }

    @PostMapping("/weekly")
    public ApiResponse<IdResponse> createWeekly(@Valid @RequestBody WeeklyReportCreateRequest request) {
        return ApiResponse.success(new IdResponse(reportService.createWeeklyReport(request)));
    }
}
