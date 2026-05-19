export interface CustomerListItem {
  id: number;
  customerCode: string;
  nickname: string;
  customerType: string;
  sourcePlatform: string;
  intentLevel: string;
  currentStage: string;
  ownerDisplayName: string;
  lastContactAt: string | null;
  nextFollowAt: string | null;
  tags: string[];
}

export interface CustomerTag {
  id: number;
  tagName: string;
  tagType: string;
  tagColor: string | null;
}

export interface CustomerQuery {
  page?: number;
  pageSize?: number;
  keyword?: string;
  intentLevel?: string;
  currentStage?: string;
  ownerUserId?: number;
  sourcePlatform?: string;
}
