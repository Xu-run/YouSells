import request from "@/utils/request";
import type { ApiResponse } from "@/types/api";
import type { DashboardOverview } from "@/types/dashboard";

export async function fetchDashboardOverview(): Promise<DashboardOverview> {
  const response = await request.get<ApiResponse<DashboardOverview>>("/dashboard/overview");
  return response.data.data;
}
