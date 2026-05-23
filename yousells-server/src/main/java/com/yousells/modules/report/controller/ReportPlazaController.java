package com.yousells.modules.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.report.dto.CreateReportCommentRequest;
import com.yousells.modules.report.service.ReportPlazaService;
import com.yousells.modules.report.vo.ReportCommentVo;
import com.yousells.modules.report.vo.ReportPlazaItemVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/plaza")
public class ReportPlazaController {

    private final ReportPlazaService reportPlazaService;

    public ReportPlazaController(ReportPlazaService reportPlazaService) {
        this.reportPlazaService = reportPlazaService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ReportPlazaItemVo>> pagePlaza(
            @RequestParam String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        var result = reportPlazaService.pagePlaza(type, userId, page, pageSize, currentUser.userId());
        return ApiResponse.success(PageResponse.of(result.getRecords(), result.getCurrent(), result.getSize(), result.getTotal()));
    }

    @PostMapping("/{reportType}/{reportId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(
            @PathVariable String reportType,
            @PathVariable Long reportId) {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        boolean liked = reportPlazaService.toggleLike(reportType, reportId, currentUser.userId());
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        return ApiResponse.success(data);
    }

    @GetMapping("/{reportType}/{reportId}/comments")
    public ApiResponse<PageResponse<ReportCommentVo>> pageComments(
            @PathVariable String reportType,
            @PathVariable Long reportId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = reportPlazaService.pageComments(reportType, reportId, page, pageSize);
        return ApiResponse.success(PageResponse.of(result.getRecords(), result.getCurrent(), result.getSize(), result.getTotal()));
    }

    @PostMapping("/{reportType}/{reportId}/comments")
    public ApiResponse<Long> createComment(
            @PathVariable String reportType,
            @PathVariable Long reportId,
            @Valid @RequestBody CreateReportCommentRequest request) {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        Long commentId = reportPlazaService.createComment(reportType, reportId, currentUser.userId(), request);
        return ApiResponse.success(commentId);
    }
}
