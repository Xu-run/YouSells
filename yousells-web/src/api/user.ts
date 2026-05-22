import request from "@/utils/request";
import type { ApiResponse, IdResponse } from "@/types/api";
import type {
  CreateUserRequest,
  UpdatePasswordRequest,
  UpdateProfileRequest,
  UpdateUserRequest,
  UserListItem,
  UserProfile
} from "@/types/user";

export async function fetchUserList(): Promise<UserListItem[]> {
  const response = await request.get<ApiResponse<UserListItem[]>>("/users");
  return response.data.data;
}

export async function fetchUserProfile(): Promise<UserProfile> {
  const response = await request.get<ApiResponse<UserProfile>>("/users/me");
  return response.data.data;
}

export async function updateUserProfile(data: UpdateProfileRequest): Promise<UserProfile> {
  const response = await request.put<ApiResponse<UserProfile>>("/users/me", data);
  return response.data.data;
}

export async function updateUserPassword(data: UpdatePasswordRequest): Promise<void> {
  await request.put<ApiResponse<null>>("/users/me/password", data);
}

export async function fetchUserDetail(userId: number): Promise<UserProfile> {
  const response = await request.get<ApiResponse<UserProfile>>(`/users/${userId}`);
  return response.data.data;
}

export async function createUser(data: CreateUserRequest): Promise<number> {
  const response = await request.post<ApiResponse<IdResponse>>("/users", data);
  return response.data.data.id;
}

export async function updateUser(userId: number, data: UpdateUserRequest): Promise<void> {
  await request.put<ApiResponse<null>>(`/users/${userId}`, data);
}
