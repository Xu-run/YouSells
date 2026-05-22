// P1 — 学生客户模型
export interface CustomerListItem {
  id: number;
  realName: string;
  grade: string;
  major: string;
  className: string | null;
  progress: string;
  intent: string;
  ownerDisplayName: string;
  inviterDisplayName: string;
  createdAt: string | null;
}

export interface CustomerQuery {
  page?: number;
  pageSize?: number;
  keyword?: string;
  grade?: string;
  major?: string;
  progress?: string;
  intent?: string;
  ownerUserId?: number;
}
