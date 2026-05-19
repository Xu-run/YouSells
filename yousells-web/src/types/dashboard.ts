export interface DashboardTaskReminder {
  taskId: number;
  taskTitle: string;
  status: string;
  ownerDisplayName: string;
  dueAt: string;
}

export interface DashboardCustomerReminder {
  customerId: number;
  nickname: string;
  intentLevel: string;
  currentStage: string;
  nextFollowAt: string;
}

export interface DashboardOverview {
  todayPendingFollowCount: number;
  overdueCustomerCount: number;
  recentNewCustomerCount: number;
  highIntentCustomerCount: number;
  todayTasks: DashboardTaskReminder[];
  focusCustomers: DashboardCustomerReminder[];
}
