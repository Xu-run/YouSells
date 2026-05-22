import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { CustomerListItem, CustomerQuery } from "@/types/customer-list";

// P1: GET /api/customers (DataScope filtered)
export async function fetchCustomers(params: CustomerQuery): Promise<PageResponse<CustomerListItem>> {
  const response = await request.get<ApiResponse<PageResponse<CustomerListItem>>>("/customers", { params });
  return response.data.data;
}
