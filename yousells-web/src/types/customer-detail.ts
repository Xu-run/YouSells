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
