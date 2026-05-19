package com.yousells.modules.dashboard.service.impl;

import com.yousells.modules.dashboard.service.DashboardService;
import com.yousells.modules.dashboard.vo.DashboardCustomerReminderVo;
import com.yousells.modules.dashboard.vo.DashboardOverviewVo;
import com.yousells.modules.dashboard.vo.DashboardTaskReminderVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardOverviewVo getOverview() {
        return new DashboardOverviewVo(
                18,
                6,
                12,
                9,
                List.of(
                        new DashboardTaskReminderVo(301L, "整理高意向客户回访清单", "IN_PROGRESS", "秦梓源", LocalDateTime.of(2026, 5, 18, 20, 0)),
                        new DashboardTaskReminderVo(302L, "补齐日报模板字段说明", "TODO", "志明", LocalDateTime.of(2026, 5, 18, 22, 0))
                ),
                List.of(
                        new DashboardCustomerReminderVo(1001L, "小林", "A", "HIGH_INTENT", LocalDateTime.of(2026, 5, 19, 12, 0)),
                        new DashboardCustomerReminderVo(1002L, "阿泽", "B", "NURTURING", LocalDateTime.of(2026, 5, 19, 20, 0))
                )
        );
    }
}
