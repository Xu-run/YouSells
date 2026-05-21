export interface DailyReport {
  id: number;
  reportDate: string;
  newCustomerCount: number;
  followUpCount: number;
  progressAdvanceCount: number;
  taskCompletedCount: number;
  summary: string;
  issues: string | null;
  tomorrowPlan: string;
}

export interface WeeklyReport {
  id: number;
  weekKey: string;
  weekStart: string;
  weekEnd: string;
  newCustomerCount: number;
  followUpCount: number;
  progressAdvanceCount: number;
  convertedCount: number;
  taskCompletedCount: number;
  summary: string;
  issues: string | null;
  nextWeekPlan: string;
}

export interface DailyReportCreateRequest {
  reportDate: string;
  summary: string;
  issues?: string | null;
  tomorrowPlan: string;
}

export interface WeeklyReportCreateRequest {
  weekKey: string;
  summary: string;
  issues?: string | null;
  nextWeekPlan: string;
}
