export const RouteName = {
  Login: "login",
  Dashboard: "dashboard",
  CustomerList: "customer-list",
  CustomerDetail: "customer-detail",
  TaskBoard: "task-board",
  DailyReport: "daily-report",
  WeeklyReport: "weekly-report",
  TopicList: "topic-list",
  TopicDetail: "topic-detail"
} as const;

export type RouteNameValue = (typeof RouteName)[keyof typeof RouteName];
