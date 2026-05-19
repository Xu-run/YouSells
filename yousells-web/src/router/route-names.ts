export const RouteName = {
  Login: "login",
  Dashboard: "dashboard",
  CustomerList: "customer-list",
  CustomerDetail: "customer-detail",
  TaskBoard: "task-board",
  DailyReport: "daily-report",
  WeeklyReport: "weekly-report",
  ScriptLibrary: "script-library"
} as const;

export type RouteNameValue = (typeof RouteName)[keyof typeof RouteName];
