export interface CustomerDetail {
  id: number;
  realName: string;
  grade: string;
  major: string;
  className: string | null;
  inviterDisplayName: string;
  ownerDisplayName: string;
  progress: string;
  intent: string;
  inviterNote: string | null;
}
