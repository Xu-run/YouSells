export interface CustomerDetail {
  id: number;
  realName: string;
  grade: string;
  major: string;
  className: string | null;
  inviterUserId: number;
  inviterDisplayName: string;
  ownerUserId: number;
  ownerDisplayName: string;
  progress: string;
  intent: string;
  inviterNote: string | null;
}
