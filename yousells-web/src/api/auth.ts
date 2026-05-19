import request from "@/utils/request";
import type { ApiResponse } from "@/types/api";
import type { CurrentUser } from "@/types/auth";

export interface LoginRequest {
  username: string;
  password: string;
}

export async function login(data: LoginRequest): Promise<CurrentUser> {
  const response = await request.post<ApiResponse<CurrentUser>>("/api/auth/login", data);
  return response.data.data;
}

export async function fetchCurrentUser(): Promise<CurrentUser> {
  const response = await request.get<ApiResponse<CurrentUser>>("/api/auth/me");
  return response.data.data;
}

export async function logout(): Promise<void> {
  await request.post<ApiResponse<null>>("/api/auth/logout");
}
