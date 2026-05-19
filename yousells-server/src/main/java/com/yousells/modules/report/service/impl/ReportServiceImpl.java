package com.yousells.modules.report.service.impl;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.service.ReportService;
import com.yousells.modules.report.vo.DailyReportVo;
import com.yousells.modules.report.vo.WeeklyReportVo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final List<DailyReportVo> sampleDailyReports = List.of(
            new DailyReportVo(7001L, LocalDate.of(2026, 5, 18), "秦梓源", "完成 P0 骨架落地", "联调环境还没拉起", "继续补测试与部署说明"),
            new DailyReportVo(7002L, LocalDate.of(2026, 5, 18), "志明", "整理客户回访数据", "客户标签口径待统一", "明天开始对接客户模块")
    );

    private final List<WeeklyReportVo> sampleWeeklyReports = List.of(
            new WeeklyReportVo(7101L, "2026-W21", "秦梓源", "确定技术选型并建立仓库", "前端环境未安装依赖", "推进基础设施与联调基线"),
            new WeeklyReportVo(7102L, "2026-W21", "哲涛", "补齐业务流程资料", "日报字段定义待确认", "进入任务模块开发")
    );

    @Override
    public PageResponse<DailyReportVo> pageDailyReports(int page, int pageSize) {
        return slice(sampleDailyReports, page, pageSize);
    }

    @Override
    public Long createDailyReport(DailyReportCreateRequest request) {
        return 7201L;
    }

    @Override
    public PageResponse<WeeklyReportVo> pageWeeklyReports(int page, int pageSize) {
        return slice(sampleWeeklyReports, page, pageSize);
    }

    @Override
    public Long createWeeklyReport(WeeklyReportCreateRequest request) {
        return 7301L;
    }

    private <T> PageResponse<T> slice(List<T> data, int page, int pageSize) {
        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 20 : pageSize;
        int fromIndex = Math.min((safePage - 1) * safePageSize, data.size());
        int toIndex = Math.min(fromIndex + safePageSize, data.size());
        return PageResponse.of(data.subList(fromIndex, toIndex), safePage, safePageSize, data.size());
    }
}
