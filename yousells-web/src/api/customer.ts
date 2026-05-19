import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { CustomerDetail, CustomerListItem, CustomerQuery, CustomerTag } from "@/types/customer";

export async function fetchCustomers(params: CustomerQuery): Promise<PageResponse<CustomerListItem>> {
  const response = await request.get<ApiResponse<PageResponse<CustomerListItem>>>("/api/customers", { params });
  return response.data.data;
}

export async function fetchCustomerDetail(id: string | number): Promise<CustomerDetail> {
  const response = await request.get<ApiResponse<CustomerDetail>>(`/api/customers/${id}`);
  return response.data.data;
}

export async function fetchCustomerTags(): Promise<CustomerTag[]> {
  const response = await request.get<ApiResponse<CustomerTag[]>>("/api/customers/tags");
  return response.data.data;
}
