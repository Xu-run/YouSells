package com.yousells.common.constant;

/**
 * 业务约定值常量 — 与数据库中实际存储的中文值保持一致
 */
public final class BusinessConstants {

    // 客户进度阶段
    public static final String STAGE_CAREER_PLAN = "职规";
    public static final String STAGE_TECH_STACK = "技术栈";
    public static final String STAGE_COURSE = "课程";
    public static final String STAGE_CLOSED = "成交";

    // 意向等级
    public static final String INTENT_HIGH = "很稳";
    public static final String INTENT_MEDIUM = "可跟";
    public static final String INTENT_LOW = "观望";
    public static final String INTENT_COLD = "冷淡";

    public static final java.util.List<String> STAGE_ORDER =
            java.util.List.of(STAGE_CAREER_PLAN, STAGE_TECH_STACK, STAGE_COURSE, STAGE_CLOSED);

    public static final java.util.List<String> INTENT_ORDER =
            java.util.List.of(INTENT_HIGH, INTENT_MEDIUM, INTENT_LOW, INTENT_COLD);

    private BusinessConstants() {
    }
}
