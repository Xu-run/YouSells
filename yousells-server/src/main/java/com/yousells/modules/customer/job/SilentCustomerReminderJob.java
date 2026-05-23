package com.yousells.modules.customer.job;

import com.yousells.common.constant.NotificationTypeConstants;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.mapper.CustomerMapper;
import com.yousells.modules.notification.entity.NotificationEntity;
import com.yousells.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SilentCustomerReminderJob {

    private final CustomerMapper customerMapper;
    private final NotificationService notificationService;

    private static final int SILENT_DAYS = 3;

    /**
     * 每天早上 9:30 扫描沉默客户
     */
    @Scheduled(cron = "0 30 9 * * ?")
    public void scanSilentCustomers() {
        log.info("Running silent customer reminder job ({} days)", SILENT_DAYS);
        LocalDateTime deadline = LocalDateTime.now().minusDays(SILENT_DAYS);

        List<CustomerEntity> customers = customerMapper.selectSilentCustomers(deadline, List.of());
        int sent = 0;
        for (CustomerEntity customer : customers) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUserId(customer.getOwnerUserId());
            notification.setType(NotificationTypeConstants.FOLLOW_UP_REMINDER);
            notification.setTitle("客户跟进提醒");
            notification.setContent("客户「" + customer.getRealName() + "」已 " + SILENT_DAYS + " 天未跟进，请及时联系。");
            notification.setBusinessType("customer");
            notification.setBusinessId(customer.getId());
            notificationService.sendNotification(notification);
            sent++;
        }
        log.info("Silent customer reminder sent {} notifications", sent);
    }
}
