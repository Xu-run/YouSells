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

export interface CustomerDetail {
  id: number;
  customerCode: string;
  customerType: string;
  nickname: string;
  contactValue: string;
  sourcePlatform: string;
  expectedMajor: string | null;
  baseLevel: string | null;
  interestDirection: string | null;
  intentLevel: string;
  currentStage: string;
  currentConcern: string | null;
  latestFeedback: string | null;
  lastContactAt: string | null;
  nextFollowAction: string | null;
  nextFollowAt: string | null;
  ownerDisplayName: string;
  assistantDisplayName: string | null;
  tags: string[];
  remarks: string | null;
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
