import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { FollowUpRecord } from "@/types/followup";

export async function fetchFollowUps(customerId: string | number): Promise<PageResponse<FollowUpRecord>> {
  const response = await request.get<ApiResponse<PageResponse<FollowUpRecord>>>("/follow-ups", {
    params: {
      customerId,
      page: 1,
      pageSize: 20
    }
  });
  return response.data.data;
}
