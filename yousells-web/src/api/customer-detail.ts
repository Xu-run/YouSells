import request from "@/utils/request";
import type { ApiResponse } from "@/types/api";
import type { CustomerDetail } from "@/types/customer-detail";

export async function fetchCustomerDetail(id: string | number): Promise<CustomerDetail> {
  const response = await request.get<ApiResponse<CustomerDetail>>(`/customers/${id}`);
  return response.data.data;
}
