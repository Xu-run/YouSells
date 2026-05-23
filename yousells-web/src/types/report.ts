export interface DailyReport {
  id: number;
  reportDate: string;
  userRealName: string;
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
  userRealName: string;
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

export interface ReportPlazaItem {
  id: number;
  type: "daily" | "weekly";
  reportDate: string | null;
  weekKey: string | null;
  userId: number;
  userRealName: string;
  userLevel: string;
  summary: string;
  issues: string | null;
  tomorrowPlan: string | null;
  nextWeekPlan: string | null;
  newCustomerCount: number;
  followUpCount: number;
  progressAdvanceCount: number;
  convertedCount: number;
  taskCompletedCount: number;
  likeCount: number;
  commentCount: number;
  likedByMe: boolean;
  createdAt: string;
  updatedAt: string;
  edited: boolean;
}

export interface ReportComment {
  id: number;
  userId: number;
  userRealName: string;
  content: string;
  createdAt: string;
}
