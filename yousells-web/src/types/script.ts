export interface ScriptCategory {
  id: number;
  categoryCode: string;
  categoryName: string;
  sortOrder: number;
}

export interface ScriptItem {
  id: number;
  categoryId: number;
  categoryName: string;
  title: string;
  applicableScene: string | null;
  status: string;
  content: string;
  updatedAt: string;
}
