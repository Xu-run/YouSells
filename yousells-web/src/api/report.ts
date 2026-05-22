import request from "@/utils/request";
import type { ApiResponse, PageResponse, IdResponse } from "@/types/api";
import type {
  DailyReport,
  WeeklyReport,
  DailyReportCreateRequest,
  WeeklyReportCreateRequest
} from "@/types/report";

// P1: GET /api/reports/daily?date=YYYY-MM-DD (required)
export async function fetchDailyReport(date: string): Promise<DailyReport> {
  const response = await request.get<ApiResponse<DailyReport>>("/reports/daily", {
    params: { date }
  });
  return response.data.data;
}

// P1: GET /api/reports/daily/history?page=&pageSize=
export async function fetchDailyReportHistory(page = 1, pageSize = 20): Promise<PageResponse<DailyReport>> {
  const response = await request.get<ApiResponse<PageResponse<DailyReport>>>("/reports/daily/history", {
    params: { page, pageSize }
  });
  return response.data.data;
}

// P1: POST /api/reports/daily
export async function createDailyReport(data: DailyReportCreateRequest): Promise<IdResponse> {
  const response = await request.post<ApiResponse<IdResponse>>("/reports/daily", data);
  return response.data.data;
}

// P1: PUT /api/reports/daily/{id}
export async function updateDailyReport(id: number, data: DailyReportCreateRequest): Promise<void> {
  await request.put<ApiResponse<null>>(`/reports/daily/${id}`, data);
}

// P1: GET /api/reports/weekly?week=2026-W21 (required)
export async function fetchWeeklyReport(week: string): Promise<WeeklyReport> {
  const response = await request.get<ApiResponse<WeeklyReport>>("/reports/weekly", {
    params: { week }
  });
  return response.data.data;
}

// P1: GET /api/reports/weekly/history?page=&pageSize=
export async function fetchWeeklyReportHistory(page = 1, pageSize = 20): Promise<PageResponse<WeeklyReport>> {
  const response = await request.get<ApiResponse<PageResponse<WeeklyReport>>>("/reports/weekly/history", {
    params: { page, pageSize }
  });
  return response.data.data;
}

// P1: POST /api/reports/weekly
export async function createWeeklyReport(data: WeeklyReportCreateRequest): Promise<IdResponse> {
  const response = await request.post<ApiResponse<IdResponse>>("/reports/weekly", data);
  return response.data.data;
}

// P1: PUT /api/reports/weekly/{id}
export async function updateWeeklyReport(id: number, data: WeeklyReportCreateRequest): Promise<void> {
  await request.put<ApiResponse<null>>(`/reports/weekly/${id}`, data);
}
