package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yousells.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface ReportAggregationMapper extends BaseMapper<DailyReportEntity> {
    int countNewCustomers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("userId") Long userId);
    int countFollowUps(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("userId") Long userId);
    int countCompletedTasks(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("userId") Long userId);
    int countConvertedCustomers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("userId") Long userId);
}
