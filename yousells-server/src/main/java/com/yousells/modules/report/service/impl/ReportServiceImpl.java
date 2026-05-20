package com.yousells.modules.report.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.report.convert.ReportConvert;
import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.entity.DailyReportEntity;
import com.yousells.modules.report.entity.WeeklyReportEntity;
import com.yousells.modules.report.mapper.DailyReportMapper;
import com.yousells.modules.report.mapper.WeeklyReportMapper;
import com.yousells.modules.report.service.ReportService;
import com.yousells.modules.report.vo.DailyReportVo;
import com.yousells.modules.report.vo.WeeklyReportVo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    private final DailyReportMapper dailyReportMapper;
    private final WeeklyReportMapper weeklyReportMapper;

    public ReportServiceImpl(DailyReportMapper dailyReportMapper,
                              WeeklyReportMapper weeklyReportMapper) {
        this.dailyReportMapper = dailyReportMapper;
        this.weeklyReportMapper = weeklyReportMapper;
    }

    @Override
    public PageResponse<DailyReportVo> pageDailyReports(int page, int pageSize) {
        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 20 : pageSize;
        IPage<DailyReportVo> result = dailyReportMapper.selectPageWithUser(
                Page.of(safePage, safePageSize));
        return PageResponse.of(result.getRecords(), safePage, safePageSize, result.getTotal());
    }

    @Override
    public Long createDailyReport(DailyReportCreateRequest request) {
        Long userId = SecurityUserContext.requireCurrentUser().userId();
        DailyReportEntity entity = ReportConvert.toDailyEntity(request, userId);
        try {
            dailyReportMapper.insert(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCodeConstants.STATUS_CONFLICT,
                    "该日期已有日报记录，请勿重复提交");
        }
        return entity.getId();
    }

    @Override
    public PageResponse<WeeklyReportVo> pageWeeklyReports(int page, int pageSize) {
        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 20 : pageSize;
        IPage<WeeklyReportVo> result = weeklyReportMapper.selectPageWithUser(
                Page.of(safePage, safePageSize));
        return PageResponse.of(result.getRecords(), safePage, safePageSize, result.getTotal());
    }

    @Override
    public Long createWeeklyReport(WeeklyReportCreateRequest request) {
        Long userId = SecurityUserContext.requireCurrentUser().userId();
        WeeklyReportEntity entity = ReportConvert.toWeeklyEntity(request, userId);
        try {
            weeklyReportMapper.insert(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCodeConstants.STATUS_CONFLICT,
                    "该周已有周报记录，请勿重复提交");
        }
        return entity.getId();
    }
}
