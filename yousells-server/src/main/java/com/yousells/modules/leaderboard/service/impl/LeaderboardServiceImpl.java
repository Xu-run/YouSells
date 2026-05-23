package com.yousells.modules.leaderboard.service.impl;

import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.leaderboard.service.LeaderboardService;
import com.yousells.modules.leaderboard.vo.LeaderboardItemVo;
import com.yousells.modules.report.mapper.ReportAggregationMapper;
import com.yousells.modules.report.vo.UserStatCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserMapper userMapper;
    private final ReportAggregationMapper reportAggregationMapper;

    @Override
    public List<LeaderboardItemVo> getWeeklyLeaderboard() {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.plusDays(1).atStartOfDay();

        List<UserEntity> users = userMapper.selectList(null);
        List<Long> activeUserIds = users.stream()
                .filter(u -> "ACTIVE".equals(u.getStatus()))
                .map(UserEntity::getId)
                .toList();

        Map<Long, Integer> newCustomerMap = batchStat(reportAggregationMapper.countNewCustomersBatch(start, end, activeUserIds));
        Map<Long, Integer> followUpMap = batchStat(reportAggregationMapper.countFollowUpsBatch(start, end, activeUserIds));
        Map<Long, Integer> convertedMap = batchStat(reportAggregationMapper.countConvertedCustomersBatch(start, end, activeUserIds));
        Map<Long, Integer> taskMap = batchStat(reportAggregationMapper.countCompletedTasksBatch(start, end, activeUserIds));

        return users.stream()
                .filter(u -> "ACTIVE".equals(u.getStatus()))
                .map(u -> {
                    LeaderboardItemVo vo = new LeaderboardItemVo();
                    vo.setUserId(u.getId());
                    vo.setRealName(u.getRealName());
                    vo.setLevel(u.getLevel());

                    int newCustomers = newCustomerMap.getOrDefault(u.getId(), 0);
                    int followUps = followUpMap.getOrDefault(u.getId(), 0);
                    int converted = convertedMap.getOrDefault(u.getId(), 0);
                    int tasks = taskMap.getOrDefault(u.getId(), 0);

                    vo.setNewCustomerCount(newCustomers);
                    vo.setFollowUpCount(followUps);
                    vo.setConvertedCount(converted);
                    vo.setTaskCompletedCount(tasks);

                    double rate = newCustomers > 0 ? (double) converted / newCustomers * 100 : 0;
                    vo.setConversionRate(Math.round(rate * 10.0) / 10.0);

                    return vo;
                })
                .sorted((a, b) -> Integer.compare(
                        b.getConvertedCount() * 100 + b.getNewCustomerCount(),
                        a.getConvertedCount() * 100 + a.getNewCustomerCount()
                ))
                .toList();
    }

    private Map<Long, Integer> batchStat(List<UserStatCount> stats) {
        return stats.stream()
                .collect(Collectors.toMap(UserStatCount::userId, UserStatCount::count, (a, b) -> a));
    }
}
