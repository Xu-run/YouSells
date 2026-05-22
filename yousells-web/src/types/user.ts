export interface UserListItem {
  userId: number;
  username: string;
  realName: string;
  level: string;
  managerUserId: number | null;
  status: string;
}

export interface UserProfile {
  userId: number;
  username: string;
  realName: string;
  level: string;
  managerUserId: number | null;
  status: string;
}

export interface UpdateProfileRequest {
  realName: string;
}

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface CreateUserRequest {
  username: string;
  realName: string;
  password: string;
  level: string;
  managerUserId?: number | null;
}

export interface UpdateUserRequest {
  realName: string;
  level: string;
  managerUserId?: number | null;
  status?: string;
}
