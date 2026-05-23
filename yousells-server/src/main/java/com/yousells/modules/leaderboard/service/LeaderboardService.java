package com.yousells.modules.leaderboard.service;

import com.yousells.modules.leaderboard.vo.LeaderboardItemVo;

import java.util.List;

public interface LeaderboardService {

    List<LeaderboardItemVo> getWeeklyLeaderboard();
}
