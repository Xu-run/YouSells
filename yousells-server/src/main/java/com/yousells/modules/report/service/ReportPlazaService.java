package com.yousells.modules.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.report.dto.CreateReportCommentRequest;
import com.yousells.modules.report.vo.ReportCommentVo;
import com.yousells.modules.report.vo.ReportPlazaItemVo;

public interface ReportPlazaService {

    Page<ReportPlazaItemVo> pagePlaza(String type, Long userId, int page, int pageSize, Long currentUserId);

    boolean toggleLike(String reportType, Long reportId, Long currentUserId);

    Page<ReportCommentVo> pageComments(String reportType, Long reportId, int page, int pageSize);

    Long createComment(String reportType, Long reportId, Long currentUserId, CreateReportCommentRequest request);
}
