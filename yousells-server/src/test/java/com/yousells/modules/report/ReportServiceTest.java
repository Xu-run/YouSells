package com.yousells.modules.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.report.dto.DailyReportCreateRequest;
import com.yousells.modules.report.dto.WeeklyReportCreateRequest;
import com.yousells.modules.report.entity.DailyReportEntity;
import com.yousells.modules.report.entity.WeeklyReportEntity;
import com.yousells.modules.report.mapper.DailyReportMapper;
import com.yousells.modules.report.mapper.WeeklyReportMapper;
import com.yousells.modules.report.service.impl.ReportServiceImpl;
import com.yousells.modules.report.vo.DailyReportVo;
import com.yousells.modules.report.vo.WeeklyReportVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    private final DailyReportMapper dailyReportMapper = mock(DailyReportMapper.class);
    private final WeeklyReportMapper weeklyReportMapper = mock(WeeklyReportMapper.class);
    private final ReportServiceImpl service = new ReportServiceImpl(dailyReportMapper, weeklyReportMapper);

    private static final LoginUser CURRENT_USER = new LoginUser(2L, "member", "普通成员", "T0", 1L);

    private MockedStatic<SecurityUserContext> securityContextMock;

    private static final List<DailyReportVo> SAMPLE_DAILY = List.of(
            new DailyReportVo(7001L, LocalDate.of(2026, 5, 18), "秦梓源",
                    "完成P0骨架落地", "联调环境还没拉起", "继续补测试与部署说明"),
            new DailyReportVo(7002L, LocalDate.of(2026, 5, 18), "志明",
                    "整理客户回访数据", "客户标签口径待统一", "明天开始对接客户模块")
    );

    private static final List<WeeklyReportVo> SAMPLE_WEEKLY = List.of(
            new WeeklyReportVo(7101L, "2026-W21", "秦梓源",
                    "确定技术选型并建立仓库", "前端环境未安装依赖", "推进基础设施与联调基线"),
            new WeeklyReportVo(7102L, "2026-W21", "哲涛",
                    "补齐业务流程资料", "日报字段定义待确认", "进入任务模块开发")
    );

    @BeforeEach
    void setUp() {
        securityContextMock = Mockito.mockStatic(SecurityUserContext.class);
        securityContextMock.when(SecurityUserContext::requireCurrentUser).thenReturn(CURRENT_USER);
    }

    @AfterEach
    void tearDown() {
        securityContextMock.close();
    }

    @Test
    void shouldPageDailyReports() {
        Page<DailyReportVo> pageResult = new Page<>(1, 20, 2);
        pageResult.setRecords(SAMPLE_DAILY);
        when(dailyReportMapper.selectPageWithUser(any())).thenReturn(pageResult);

        PageResponse<DailyReportVo> result = service.pageDailyReports(1, 20);

        assertEquals(2, result.total());
        assertEquals(1, result.page());
        assertEquals(20, result.pageSize());
        assertEquals(2, result.list().size());
    }

    @Test
    void shouldReturnEmptyDailyReports() {
        Page<DailyReportVo> pageResult = new Page<>(1, 20, 0);
        pageResult.setRecords(List.of());
        when(dailyReportMapper.selectPageWithUser(any())).thenReturn(pageResult);

        PageResponse<DailyReportVo> result = service.pageDailyReports(1, 20);

        assertEquals(0, result.total());
        assertTrue(result.list().isEmpty());
    }

    @Test
    void shouldCreateDailyReport() {
        DailyReportCreateRequest request = new DailyReportCreateRequest(
                LocalDate.of(2026, 5, 20), "完成任务模块", "无", "开始报表模块"
        );
        when(dailyReportMapper.insert(Mockito.<DailyReportEntity>any())).thenAnswer(invocation -> {
            DailyReportEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        });

        Long id = service.createDailyReport(request);

        assertEquals(100L, id);
        verify(dailyReportMapper).insert(Mockito.<DailyReportEntity>argThat(entity ->
                entity.getReportDate().equals(LocalDate.of(2026, 5, 20))
                        && entity.getUserId().equals(2L)
                        && "完成任务模块".equals(entity.getTodayWork())
                        && "开始报表模块".equals(entity.getTomorrowPlan())
        ));
    }

    @Test
    void shouldThrowConflictWhenDuplicateDailyReport() {
        DailyReportCreateRequest request = new DailyReportCreateRequest(
                LocalDate.of(2026, 5, 20), "内容", null, "计划"
        );
        when(dailyReportMapper.insert(Mockito.<DailyReportEntity>any()))
                .thenThrow(new DuplicateKeyException("Duplicate entry"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.createDailyReport(request));

        assertEquals(ErrorCodeConstants.STATUS_CONFLICT, exception.getCode());
        assertTrue(exception.getMessage().contains("重复提交"));
    }

    @Test
    void shouldPageWeeklyReports() {
        Page<WeeklyReportVo> pageResult = new Page<>(1, 20, 2);
        pageResult.setRecords(SAMPLE_WEEKLY);
        when(weeklyReportMapper.selectPageWithUser(any())).thenReturn(pageResult);

        PageResponse<WeeklyReportVo> result = service.pageWeeklyReports(1, 20);

        assertEquals(2, result.total());
        assertEquals(2, result.list().size());
    }

    @Test
    void shouldReturnEmptyWeeklyReports() {
        Page<WeeklyReportVo> pageResult = new Page<>(1, 20, 0);
        pageResult.setRecords(List.of());
        when(weeklyReportMapper.selectPageWithUser(any())).thenReturn(pageResult);

        PageResponse<WeeklyReportVo> result = service.pageWeeklyReports(1, 20);

        assertEquals(0, result.total());
        assertTrue(result.list().isEmpty());
    }

    @Test
    void shouldCreateWeeklyReport() {
        WeeklyReportCreateRequest request = new WeeklyReportCreateRequest(
                "2026-W21", "本周完成两个模块", "环境问题", "下周完成剩余模块"
        );
        when(weeklyReportMapper.insert(Mockito.<WeeklyReportEntity>any())).thenAnswer(invocation -> {
            WeeklyReportEntity entity = invocation.getArgument(0);
            entity.setId(200L);
            return 1;
        });

        Long id = service.createWeeklyReport(request);

        assertEquals(200L, id);
        verify(weeklyReportMapper).insert(Mockito.<WeeklyReportEntity>argThat(entity ->
                "2026-W21".equals(entity.getWeekKey())
                        && entity.getUserId().equals(2L)
                        && "本周完成两个模块".equals(entity.getWeeklySummary())
        ));
    }

    @Test
    void shouldThrowConflictWhenDuplicateWeeklyReport() {
        WeeklyReportCreateRequest request = new WeeklyReportCreateRequest(
                "2026-W21", "内容", null, "计划"
        );
        when(weeklyReportMapper.insert(Mockito.<WeeklyReportEntity>any()))
                .thenThrow(new DuplicateKeyException("Duplicate entry"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.createWeeklyReport(request));

        assertEquals(ErrorCodeConstants.STATUS_CONFLICT, exception.getCode());
        assertTrue(exception.getMessage().contains("重复提交"));
    }
}
