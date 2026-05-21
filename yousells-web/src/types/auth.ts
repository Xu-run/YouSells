export interface CurrentUser {
  userId: number;
  username: string;
  realName: string;
  level: string;
  managerUserId: number | null;
}

export interface LoginResult {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  userInfo: CurrentUser;
}
