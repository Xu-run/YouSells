SET NAMES utf8mb4;

-- ============================================================
-- YouSells P2.5 Schema — 报告广场（点赞 + 评论）
-- ============================================================

-- 报告点赞表
CREATE TABLE IF NOT EXISTS report_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '点赞人ID',
    report_type VARCHAR(8) NOT NULL COMMENT 'daily | weekly',
    report_id BIGINT NOT NULL COMMENT '报告ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_report_likes (user_id, report_type, report_id),
    KEY idx_report_likes_report (report_type, report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 报告评论表
CREATE TABLE IF NOT EXISTS report_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '评论人ID',
    report_type VARCHAR(8) NOT NULL COMMENT 'daily | weekly',
    report_id BIGINT NOT NULL COMMENT '报告ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_report_comments_report (report_type, report_id, created_at),
    KEY idx_report_comments_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
