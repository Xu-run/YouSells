import request from "@/utils/request";
import type { ApiResponse, PageResponse, IdResponse } from "@/types/api";
import type { CustomerListItem, CustomerQuery, CustomerCreateRequest } from "@/types/customer-list";

// P1: GET /api/customers (DataScope filtered)
export async function fetchCustomers(
  params: CustomerQuery
): Promise<PageResponse<CustomerListItem>> {
  const response = await request.get<ApiResponse<PageResponse<CustomerListItem>>>("/customers", {
    params
  });
  return response.data.data;
}

export async function createCustomer(data: CustomerCreateRequest): Promise<IdResponse> {
  const response = await request.post<ApiResponse<IdResponse>>("/customers", data);
  return response.data.data;
}
