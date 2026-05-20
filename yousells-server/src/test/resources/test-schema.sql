CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    real_name VARCHAR(64) NULL,
    phone VARCHAR(32) NULL,
    email VARCHAR(128) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(64) NOT NULL,
    customer_type VARCHAR(32) NOT NULL,
    nickname VARCHAR(128) NOT NULL,
    contact_value VARCHAR(128) NOT NULL,
    source_platform VARCHAR(64) NOT NULL,
    added_at DATETIME NULL,
    is_nuist_freshman INT NOT NULL DEFAULT 0,
    expected_major VARCHAR(128) NULL,
    base_level VARCHAR(64) NULL,
    interest_direction VARCHAR(255) NULL,
    intent_level VARCHAR(32) NOT NULL,
    current_stage VARCHAR(64) NOT NULL,
    current_concern VARCHAR(255) NULL,
    latest_feedback VARCHAR(500) NULL,
    last_contact_at DATETIME NULL,
    next_follow_action VARCHAR(255) NULL,
    next_follow_at DATETIME NULL,
    owner_user_id BIGINT NOT NULL,
    assistant_user_id BIGINT NULL,
    needs_support INT NOT NULL DEFAULT 0,
    conversion_result VARCHAR(64) NULL,
    remarks TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    is_deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_customers_customer_code (customer_code)
);

CREATE TABLE IF NOT EXISTS customer_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(64) NOT NULL,
    tag_type VARCHAR(32) NOT NULL,
    tag_color VARCHAR(32) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_customer_tags_name_type (tag_name, tag_type)
);

CREATE TABLE IF NOT EXISTS customer_tag_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_customer_tag_relations (customer_id, tag_id)
);

CREATE TABLE IF NOT EXISTS customer_follow_ups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    follow_type VARCHAR(32) NOT NULL,
    communicated_content TEXT NOT NULL,
    customer_feedback VARCHAR(500) NULL,
    current_concern VARCHAR(255) NULL,
    next_action VARCHAR(255) NULL,
    next_follow_at DATETIME NULL,
    operator_user_id BIGINT NOT NULL,
    owner_user_id BIGINT NOT NULL,
    stage_before VARCHAR(64) NULL,
    stage_after VARCHAR(64) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS task_boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_title VARCHAR(255) NOT NULL,
    task_type VARCHAR(64) NULL,
    task_description TEXT NULL,
    status VARCHAR(32) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    owner_user_id BIGINT NOT NULL,
    assistant_user_id BIGINT NULL,
    start_at DATETIME NULL,
    due_at DATETIME NULL,
    next_action VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS daily_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    today_work TEXT NOT NULL,
    issues TEXT NULL,
    tomorrow_plan TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_daily_reports (report_date, user_id)
);

CREATE TABLE IF NOT EXISTS weekly_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_key VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    weekly_summary TEXT NOT NULL,
    issues TEXT NULL,
    next_week_plan TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_weekly_reports (week_key, user_id)
);

CREATE TABLE IF NOT EXISTS script_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code VARCHAR(64) NOT NULL,
    category_name VARCHAR(64) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS scripts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    applicable_scene VARCHAR(255) NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);
