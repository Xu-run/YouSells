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

export interface ProgressDistributionItem {
  stage: string;
  count: number;
}

export interface IntentDistributionItem {
  intent: string;
  count: number;
}

export interface TrendDataPoint {
  date: string;
  count: number;
}

export interface DashboardOverview {
  todayPendingFollowCount: number;
  overdueCustomerCount: number;
  recentNewCustomerCount: number;
  highIntentCustomerCount: number;
  totalCustomerCount: number;
  monthlyClosedCount: number;
  progressDistribution: ProgressDistributionItem[];
  intentDistribution: IntentDistributionItem[];
  trendData: TrendDataPoint[];
  todayTasks: DashboardTaskReminder[];
  focusCustomers: DashboardCustomerReminder[];
}
