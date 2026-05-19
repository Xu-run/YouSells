export interface CurrentUser {
  userId: number;
  username: string;
  displayName: string;
  roles: string[];
}

export interface LoginResult {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  userInfo: CurrentUser;
}
