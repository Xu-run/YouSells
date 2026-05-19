import request from "@/utils/request";
import type { ApiResponse } from "@/types/api";
import type { CurrentUser, LoginResult } from "@/types/auth";

export interface LoginRequest {
  username: string;
  password: string;
}

export async function login(data: LoginRequest): Promise<LoginResult> {
  const response = await request.post<ApiResponse<LoginResult>>("/auth/login", data);
  return response.data.data;
}

export async function fetchCurrentUser(): Promise<CurrentUser> {
  const response = await request.get<ApiResponse<CurrentUser>>("/auth/me");
  return response.data.data;
}

export async function logout(): Promise<void> {
  await request.post<ApiResponse<null>>("/auth/logout");
}
