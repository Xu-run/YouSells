package com.yousells.modules.leaderboard.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.modules.leaderboard.service.LeaderboardService;
import com.yousells.modules.leaderboard.vo.LeaderboardItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ApiResponse<List<LeaderboardItemVo>> getWeeklyLeaderboard() {
        return ApiResponse.success(leaderboardService.getWeeklyLeaderboard());
    }
}
