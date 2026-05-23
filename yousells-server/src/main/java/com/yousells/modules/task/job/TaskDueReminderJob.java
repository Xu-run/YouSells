package com.yousells.modules.task.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yousells.common.constant.NotificationTypeConstants;
import com.yousells.modules.notification.entity.NotificationEntity;
import com.yousells.modules.notification.service.NotificationService;
import com.yousells.modules.task.entity.TaskBoardEntity;
import com.yousells.modules.task.mapper.TaskBoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDueReminderJob {

    private final TaskBoardMapper taskBoardMapper;
    private final NotificationService notificationService;

    /**
     * 每天早上 9:00 扫描即将到期的任务
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void morningReminder() {
        log.info("Running morning task due reminder job");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        LambdaQueryWrapper<TaskBoardEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(TaskBoardEntity::getStatus, "已完成")
                .le(TaskBoardEntity::getDueAt, tomorrow)
                .ge(TaskBoardEntity::getDueAt, now)
                .eq(TaskBoardEntity::getIsDeleted, 0);

        List<TaskBoardEntity> tasks = taskBoardMapper.selectList(wrapper);
        for (TaskBoardEntity task : tasks) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUserId(task.getOwnerUserId());
            notification.setType(NotificationTypeConstants.TASK_DUE_SOON);
            notification.setTitle("任务即将到期");
            notification.setContent("任务「" + task.getTaskTitle() + "」将在今天到期，请及时处理。");
            notification.setBusinessType("task");
            notification.setBusinessId(task.getId());
            notificationService.sendNotification(notification);
        }
        log.info("Morning reminder sent {} notifications", tasks.size());
    }

    /**
     * 每小时的 0 分检查 24h 内到期的任务（首次提醒）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyReminder() {
        log.info("Running hourly task due reminder job");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(23);
        LocalDateTime windowEnd = now.plusHours(25);

        LambdaQueryWrapper<TaskBoardEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(TaskBoardEntity::getStatus, "已完成")
                .between(TaskBoardEntity::getDueAt, windowStart, windowEnd)
                .eq(TaskBoardEntity::getIsDeleted, 0);

        List<TaskBoardEntity> tasks = taskBoardMapper.selectList(wrapper);
        for (TaskBoardEntity task : tasks) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUserId(task.getOwnerUserId());
            notification.setType(NotificationTypeConstants.TASK_DUE_SOON);
            notification.setTitle("任务即将到期提醒");
            notification.setContent("任务「" + task.getTaskTitle() + "」将在 24 小时内到期。");
            notification.setBusinessType("task");
            notification.setBusinessId(task.getId());
            notificationService.sendNotification(notification);
        }
        log.info("Hourly reminder sent {} notifications", tasks.size());
    }
}
