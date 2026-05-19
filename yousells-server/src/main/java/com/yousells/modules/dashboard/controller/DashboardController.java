package com.yousells.modules.dashboard.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.modules.dashboard.service.DashboardService;
import com.yousells.modules.dashboard.vo.DashboardOverviewVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVo> overview() {
        return ApiResponse.success(dashboardService.getOverview());
    }
}
