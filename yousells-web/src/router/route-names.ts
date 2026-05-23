export const RouteName = {
  Login: "login",
  Dashboard: "dashboard",
  CustomerList: "customer-list",
  CustomerDetail: "customer-detail",
  TaskBoard: "task-board",
  DailyReport: "daily-report",
  WeeklyReport: "weekly-report",
  ReportPlaza: "report-plaza",
  TopicList: "topic-list",
  TopicDetail: "topic-detail",
  Profile: "profile",
  MemberManage: "member-manage",
  Leaderboard: "leaderboard",
  NotificationList: "notification-list"
} as const;

export type RouteNameValue = (typeof RouteName)[keyof typeof RouteName];
