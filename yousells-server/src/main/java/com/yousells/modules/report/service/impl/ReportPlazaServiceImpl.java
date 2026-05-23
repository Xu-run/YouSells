package com.yousells.modules.report.service.impl;

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
import com.yousells.modules.report.service.ReportPlazaService;
import com.yousells.modules.report.vo.ReportCommentVo;
import com.yousells.modules.report.vo.ReportPlazaItemVo;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportPlazaServiceImpl implements ReportPlazaService {

    private final ReportPlazaMapper reportPlazaMapper;
    private final ReportLikeMapper reportLikeMapper;
    private final ReportCommentMapper reportCommentMapper;
    private final UserMapper userMapper;

    public ReportPlazaServiceImpl(ReportPlazaMapper reportPlazaMapper,
                                   ReportLikeMapper reportLikeMapper,
                                   ReportCommentMapper reportCommentMapper,
                                   UserMapper userMapper) {
        this.reportPlazaMapper = reportPlazaMapper;
        this.reportLikeMapper = reportLikeMapper;
        this.reportCommentMapper = reportCommentMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Page<ReportPlazaItemVo> pagePlaza(String type, Long userId, int page, int pageSize, Long currentUserId) {
        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 5 : pageSize;
        Page<ReportPlazaItemVo> pageParam = Page.of(safePage, safePageSize);

        com.baomidou.mybatisplus.core.metadata.IPage<ReportPlazaItemVo> result;
        if ("daily".equals(type)) {
            result = reportPlazaMapper.selectDailyPlaza(pageParam, userId, currentUserId);
        } else if ("weekly".equals(type)) {
            result = reportPlazaMapper.selectWeeklyPlaza(pageParam, userId, currentUserId);
        } else {
            throw new BusinessException(ErrorCodeConstants.BAD_REQUEST, "invalid report type");
        }
        Page<ReportPlazaItemVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords());
        return voPage;
    }

    @Override
    public boolean toggleLike(String reportType, Long reportId, Long currentUserId) {
        Long likeId = reportLikeMapper.findByUserAndReport(currentUserId, reportType, reportId);
        if (likeId != null) {
            reportLikeMapper.deleteById(likeId);
            return false;
        }
        ReportLikeEntity entity = new ReportLikeEntity();
        entity.setUserId(currentUserId);
        entity.setReportType(reportType);
        entity.setReportId(reportId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        reportLikeMapper.insert(entity);
        return true;
    }

    @Override
    public Page<ReportCommentVo> pageComments(String reportType, Long reportId, int page, int pageSize) {
        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 20 : pageSize;
        Page<ReportCommentVo> pageParam = Page.of(safePage, safePageSize);

        List<ReportCommentEntity> entities = reportCommentMapper.selectList(
                new LambdaQueryWrapper<ReportCommentEntity>()
                        .eq(ReportCommentEntity::getReportType, reportType)
                        .eq(ReportCommentEntity::getReportId, reportId)
                        .eq(ReportCommentEntity::getIsDeleted, 0)
                        .orderByDesc(ReportCommentEntity::getCreatedAt)
        );

        List<Long> userIds = entities.stream().map(ReportCommentEntity::getUserId).distinct().toList();
        Map<Long, String> userNameMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(UserEntity::getId, UserEntity::getRealName));

        List<ReportCommentVo> records = entities.stream().map(e -> {
            ReportCommentVo vo = new ReportCommentVo();
            vo.setId(e.getId());
            vo.setUserId(e.getUserId());
            vo.setUserRealName(userNameMap.getOrDefault(e.getUserId(), ""));
            vo.setContent(e.getContent());
            vo.setCreatedAt(e.getCreatedAt());
            return vo;
        }).toList();

        long total = reportCommentMapper.countByReport(reportType, reportId);
        Page<ReportCommentVo> result = new Page<>(safePage, safePageSize, total);
        result.setRecords(records);
        return result;
    }

    @Override
    public Long createComment(String reportType, Long reportId, Long currentUserId, CreateReportCommentRequest request) {
        ReportCommentEntity entity = new ReportCommentEntity();
        entity.setUserId(currentUserId);
        entity.setReportType(reportType);
        entity.setReportId(reportId);
        entity.setContent(request.content());
        entity.setIsDeleted(0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        reportCommentMapper.insert(entity);
        return entity.getId();
    }
}
