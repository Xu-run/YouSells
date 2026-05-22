package com.yousells.modules.dashboard.service.impl;

import com.yousells.modules.customer.dto.CustomerQueryRequest;
import com.yousells.modules.customer.service.CustomerService;
import com.yousells.modules.customer.vo.CustomerListItemVo;
import com.yousells.modules.dashboard.service.DashboardService;
import com.yousells.modules.dashboard.vo.DashboardCustomerReminderVo;
import com.yousells.modules.dashboard.vo.DashboardOverviewVo;
import com.yousells.modules.dashboard.vo.DashboardTaskReminderVo;
import com.yousells.modules.dashboard.vo.IntentDistributionItem;
import com.yousells.modules.dashboard.vo.ProgressDistributionItem;
import com.yousells.modules.dashboard.vo.TrendDataPoint;
import com.yousells.common.constant.BusinessConstants;
import com.yousells.modules.task.dto.TaskQueryRequest;
import com.yousells.modules.task.service.TaskBoardService;
import com.yousells.modules.task.vo.TaskBoardItemVo;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int DASHBOARD_FETCH_SIZE = 1000;
    private static final int TASK_REMINDER_LIMIT = 5;
    private static final int FOCUS_CUSTOMER_LIMIT = 5;
    private static final int RECENT_CUSTOMER_DAYS = 7;
    private static final int TREND_DAYS = 30;

    private final CustomerService customerService;
    private final TaskBoardService taskBoardService;
    private final Clock clock;

    public DashboardServiceImpl(CustomerService customerService,
                                TaskBoardService taskBoardService,
                                Clock clock) {
        this.customerService = customerService;
        this.taskBoardService = taskBoardService;
        this.clock = clock;
    }

    @Override
    public DashboardOverviewVo getOverview() {
        LocalDate today = LocalDate.now(clock);
        List<CustomerListItemVo> customers = fetchCustomers();
        List<TaskBoardItemVo> tasks = fetchTasks();

        return new DashboardOverviewVo(
                countTodayPendingFollows(customers),
                countOverdueCustomers(customers),
                countRecentCustomers(customers, today),
                countHighIntentCustomers(customers),
                customers.size(),
                countMonthlyClosedCustomers(customers, today),
                buildProgressDistribution(customers),
                buildIntentDistribution(customers),
                buildTrendData(customers, today),
                buildTaskReminders(tasks),
                buildFocusCustomers(customers)
        );
    }

    private List<CustomerListItemVo> fetchCustomers() {
        return customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, DASHBOARD_FETCH_SIZE)).list();
    }

    private List<TaskBoardItemVo> fetchTasks() {
        return taskBoardService.pageTasks(new TaskQueryRequest(1, DASHBOARD_FETCH_SIZE, null, null, null)).list();
    }

    private int countTodayPendingFollows(List<CustomerListItemVo> customers) {
        return (int) customers.stream()
                .filter(c -> !BusinessConstants.STAGE_COURSE.equals(c.progress())
                        && !BusinessConstants.STAGE_CLOSED.equals(c.progress()))
                .count();
    }

    private int countOverdueCustomers(List<CustomerListItemVo> customers) {
        return 0;
    }

    private int countRecentCustomers(List<CustomerListItemVo> customers, LocalDate today) {
        LocalDate windowStart = today.minusDays(RECENT_CUSTOMER_DAYS - 1L);
        return (int) customers.stream()
                .map(CustomerListItemVo::createdAt)
                .filter(createdAt -> createdAt != null)
                .filter(createdAt -> {
                    LocalDate createdDate = createdAt.toLocalDate();
                    return !createdDate.isBefore(windowStart) && !createdDate.isAfter(today);
                })
                .count();
    }

    private int countHighIntentCustomers(List<CustomerListItemVo> customers) {
        return (int) customers.stream()
                .filter(c -> BusinessConstants.INTENT_HIGH.equals(c.intent())
                        || BusinessConstants.INTENT_MEDIUM.equals(c.intent()))
                .count();
    }

    private int countMonthlyClosedCustomers(List<CustomerListItemVo> customers, LocalDate today) {
        LocalDate monthStart = today.withDayOfMonth(1);
        return (int) customers.stream()
                .filter(c -> BusinessConstants.STAGE_CLOSED.equals(c.progress()))
                .map(CustomerListItemVo::createdAt)
                .filter(createdAt -> createdAt != null)
                .filter(createdAt -> {
                    LocalDate createdDate = createdAt.toLocalDate();
                    return !createdDate.isBefore(monthStart) && !createdDate.isAfter(today);
                })
                .count();
    }

    private List<ProgressDistributionItem> buildProgressDistribution(List<CustomerListItemVo> customers) {
        Map<String, Long> grouped = customers.stream()
                .collect(Collectors.groupingBy(
                        c -> c.progress() != null ? c.progress() : "未知",
                        Collectors.counting()
                ));
        // Preserve order: 职规 -> 技术栈 -> 课程 -> 成交
        List<String> stageOrder = BusinessConstants.STAGE_ORDER;
        return stageOrder.stream()
                .filter(grouped::containsKey)
                .map(stage -> new ProgressDistributionItem(stage, grouped.get(stage).intValue()))
                .toList();
    }

    private List<IntentDistributionItem> buildIntentDistribution(List<CustomerListItemVo> customers) {
        Map<String, Long> grouped = customers.stream()
                .collect(Collectors.groupingBy(
                        c -> c.intent() != null ? c.intent() : "未知",
                        Collectors.counting()
                ));
        List<String> intentOrder = BusinessConstants.INTENT_ORDER;
        return intentOrder.stream()
                .filter(grouped::containsKey)
                .map(intent -> new IntentDistributionItem(intent, grouped.get(intent).intValue()))
                .toList();
    }

    private List<TrendDataPoint> buildTrendData(List<CustomerListItemVo> customers, LocalDate today) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        LocalDate startDate = today.minusDays(TREND_DAYS - 1L);

        // Group customers by creation date
        Map<LocalDate, Long> dailyCounts = customers.stream()
                .map(CustomerListItemVo::createdAt)
                .filter(createdAt -> createdAt != null)
                .map(LocalDate::from)
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(today))
                .collect(Collectors.groupingBy(
                        date -> date,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        // Build continuous date range
        List<TrendDataPoint> result = new java.util.ArrayList<>();
        for (int i = 0; i < TREND_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateLabel = date.format(formatter);
            int count = dailyCounts.getOrDefault(date, 0L).intValue();
            result.add(new TrendDataPoint(dateLabel, count));
        }
        return result;
    }

    private List<DashboardTaskReminderVo> buildTaskReminders(List<TaskBoardItemVo> tasks) {
        return tasks.stream()
                .filter(task -> !"已完成".equals(task.status()))
                .sorted(Comparator.comparing(TaskBoardItemVo::dueAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(TASK_REMINDER_LIMIT)
                .map(task -> new DashboardTaskReminderVo(
                        task.id(),
                        task.taskTitle(),
                        task.status(),
                        task.ownerDisplayName(),
                        task.dueAt()
                ))
                .toList();
    }

    private List<DashboardCustomerReminderVo> buildFocusCustomers(List<CustomerListItemVo> customers) {
        return customers.stream()
                .filter(c -> BusinessConstants.INTENT_HIGH.equals(c.intent())
                        || BusinessConstants.INTENT_MEDIUM.equals(c.intent()))
                .limit(FOCUS_CUSTOMER_LIMIT)
                .map(customer -> new DashboardCustomerReminderVo(
                        customer.id(),
                        customer.realName(),
                        customer.intent(),
                        customer.progress(),
                        null
                ))
                .toList();
    }
}
