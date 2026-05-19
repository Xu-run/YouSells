export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  list: T[];
  page: number;
  pageSize: number;
  total: number;
}

export interface IdResponse {
  id: number;
}
