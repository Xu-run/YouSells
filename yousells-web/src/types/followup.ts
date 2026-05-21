export interface FollowUpRecord {
  id: number;
  customerId: number;
  userRealName: string;
  progress: string;
  content: string;
  feedback: string | null;
  nextAction: string | null;
  createdAt: string;
}

export interface FollowUpCreateRequest {
  customerId: number;
  progress: string;
  content: string;
  feedback?: string | null;
  nextAction?: string | null;
}
