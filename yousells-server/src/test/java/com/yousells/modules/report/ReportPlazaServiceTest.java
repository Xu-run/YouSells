package com.yousells.modules.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.modules.report.dto.CreateReportCommentRequest;
import com.yousells.modules.report.entity.ReportCommentEntity;
import com.yousells.modules.report.entity.ReportLikeEntity;
import com.yousells.modules.report.mapper.ReportCommentMapper;
import com.yousells.modules.report.mapper.ReportLikeMapper;
import com.yousells.modules.report.mapper.ReportPlazaMapper;
import com.yousells.modules.report.service.impl.ReportPlazaServiceImpl;
import com.yousells.modules.report.vo.ReportCommentVo;
import com.yousells.modules.report.vo.ReportPlazaItemVo;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportPlazaServiceTest {

    @Mock
    private ReportPlazaMapper reportPlazaMapper;

    @Mock
    private ReportLikeMapper reportLikeMapper;

    @Mock
    private ReportCommentMapper reportCommentMapper;

    @Mock
    private UserMapper userMapper;

    private ReportPlazaServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ReportPlazaServiceImpl(reportPlazaMapper, reportLikeMapper, reportCommentMapper, userMapper);
    }

    @Test
    void shouldPageDailyPlaza() {
        ReportPlazaItemVo item = new ReportPlazaItemVo();
        item.setId(1L);
        item.setType("daily");
        item.setUserRealName("秦梓源");

        Page<ReportPlazaItemVo> pageResult = new Page<>(1, 5, 1);
        pageResult.setRecords(List.of(item));

        when(reportPlazaMapper.selectDailyPlaza(any(Page.class), eq(2L), eq(1L))).thenReturn(pageResult);

        Page<ReportPlazaItemVo> result = service.pagePlaza("daily", 2L, 1, 5, 1L);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("秦梓源", result.getRecords().get(0).getUserRealName());
    }

    @Test
    void shouldPageWeeklyPlaza() {
        ReportPlazaItemVo item = new ReportPlazaItemVo();
        item.setId(2L);
        item.setType("weekly");
        item.setUserRealName("成员A");

        Page<ReportPlazaItemVo> pageResult = new Page<>(1, 5, 1);
        pageResult.setRecords(List.of(item));

        when(reportPlazaMapper.selectWeeklyPlaza(any(Page.class), eq(null), eq(1L))).thenReturn(pageResult);

        Page<ReportPlazaItemVo> result = service.pagePlaza("weekly", null, 1, 5, 1L);

        assertEquals(1, result.getTotal());
        assertEquals("成员A", result.getRecords().get(0).getUserRealName());
    }

    @Test
    void shouldRejectInvalidType() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.pagePlaza("invalid", null, 1, 5, 1L));
        assertEquals(ErrorCodeConstants.BAD_REQUEST, ex.getCode());
        assertTrue(ex.getMessage().contains("invalid report type"));
    }

    @Test
    void shouldToggleLikeWhenNotExists() {
        when(reportLikeMapper.findByUserAndReport(1L, "daily", 10L)).thenReturn(null);
        when(reportLikeMapper.insert(Mockito.<ReportLikeEntity>any())).thenAnswer(inv -> {
            ReportLikeEntity e = inv.getArgument(0);
            e.setId(100L);
            return 1;
        });

        boolean liked = service.toggleLike("daily", 10L, 1L);

        assertTrue(liked);
        verify(reportLikeMapper).insert(Mockito.<ReportLikeEntity>argThat(e ->
                e.getUserId().equals(1L) && e.getReportType().equals("daily") && e.getReportId().equals(10L)));
    }

    @Test
    void shouldToggleUnlikeWhenExists() {
        when(reportLikeMapper.findByUserAndReport(1L, "weekly", 20L)).thenReturn(50L);

        boolean liked = service.toggleLike("weekly", 20L, 1L);

        assertFalse(liked);
        verify(reportLikeMapper).deleteById(50L);
    }

    @Test
    void shouldPageComments() {
        ReportCommentEntity c1 = new ReportCommentEntity();
        c1.setId(1L);
        c1.setUserId(2L);
        c1.setContent("干得不错");
        c1.setCreatedAt(LocalDateTime.of(2026, 5, 23, 10, 0));

        ReportCommentEntity c2 = new ReportCommentEntity();
        c2.setId(2L);
        c2.setUserId(3L);
        c2.setContent("继续加油");
        c2.setCreatedAt(LocalDateTime.of(2026, 5, 23, 11, 0));

        UserEntity u2 = new UserEntity();
        u2.setId(2L);
        u2.setRealName("成员A");
        UserEntity u3 = new UserEntity();
        u3.setId(3L);
        u3.setRealName("成员B");

        when(reportCommentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(c1, c2));
        when(reportCommentMapper.countByReport("daily", 10L)).thenReturn(2);
        when(userMapper.selectBatchIds(anyList())).thenReturn(List.of(u2, u3));

        Page<ReportCommentVo> result = service.pageComments("daily", 10L, 1, 20);

        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRecords().size());
        assertEquals("干得不错", result.getRecords().get(0).getContent());
        assertEquals("成员A", result.getRecords().get(0).getUserRealName());
        assertEquals("继续加油", result.getRecords().get(1).getContent());
        assertEquals("成员B", result.getRecords().get(1).getUserRealName());
    }

    @Test
    void shouldCreateComment() {
        when(reportCommentMapper.insert(Mockito.<ReportCommentEntity>any())).thenAnswer(inv -> {
            ReportCommentEntity e = inv.getArgument(0);
            e.setId(99L);
            return 1;
        });

        CreateReportCommentRequest request = new CreateReportCommentRequest("非常有价值的报告");
        Long commentId = service.createComment("daily", 10L, 1L, request);

        assertEquals(99L, commentId);
        verify(reportCommentMapper).insert(Mockito.<ReportCommentEntity>argThat(e ->
                e.getUserId().equals(1L)
                        && e.getReportType().equals("daily")
                        && e.getReportId().equals(10L)
                        && e.getContent().equals("非常有价值的报告")
                        && e.getIsDeleted().equals(0)));
    }
}
