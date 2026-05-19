import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { CustomerListItem, CustomerQuery, CustomerTag } from "@/types/customer-list";

export async function fetchCustomers(params: CustomerQuery): Promise<PageResponse<CustomerListItem>> {
  const response = await request.get<ApiResponse<PageResponse<CustomerListItem>>>("/customers", { params });
  return response.data.data;
}

export async function fetchCustomerTags(): Promise<CustomerTag[]> {
  const response = await request.get<ApiResponse<CustomerTag[]>>("/customers/tags");
  return response.data.data;
}
