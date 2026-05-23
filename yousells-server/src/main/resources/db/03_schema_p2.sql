SET NAMES utf8mb4;

-- ============================================================
-- YouSells P2 Schema — 通知中心 + 来源渠道
-- ============================================================

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收人ID',
    type VARCHAR(32) NOT NULL COMMENT '通知类型：TASK_ASSIGNED/TASK_DUE_SOON/TASK_STATUS_CHANGED/FOLLOW_UP_REMINDER/CUSTOMER_ASSIGNED/DAILY_REPORT_VIEWED/TOPIC_REPLIED/TOPIC_MARKED_SOLUTION/SYSTEM_ANNOUNCEMENT',
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    business_type VARCHAR(32) COMMENT '关联业务类型：task/customer/report/topic',
    business_id BIGINT COMMENT '关联业务ID',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_notifications_user (user_id, is_read, created_at),
    KEY idx_notifications_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 客户表增加来源渠道字段（P2 补充）
-- 注意：MySQL 8 不支持 ADD COLUMN IF NOT EXISTS，下方使用存储过程方式保证幂等
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS AddCustomerColumns()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_schema = DATABASE() 
                     AND table_name = 'customers' 
                     AND column_name = 'source_channel') THEN
        ALTER TABLE customers ADD COLUMN source_channel VARCHAR(32) NULL COMMENT '来源渠道：校园地推/知乎/小红书/抖音/转介绍/其他' AFTER inviter_note;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_schema = DATABASE() 
                     AND table_name = 'customers' 
                     AND column_name = 'phone') THEN
        ALTER TABLE customers ADD COLUMN phone VARCHAR(20) NULL COMMENT '手机号' AFTER class_name;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_schema = DATABASE() 
                     AND table_name = 'customers' 
                     AND column_name = 'wechat') THEN
        ALTER TABLE customers ADD COLUMN wechat VARCHAR(64) NULL COMMENT '微信号' AFTER phone;
    END IF;
END$$
DELIMITER ;
CALL AddCustomerColumns();
DROP PROCEDURE IF EXISTS AddCustomerColumns;
