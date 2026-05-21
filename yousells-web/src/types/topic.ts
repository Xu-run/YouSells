export interface TopicListItem {
  id: number;
  title: string;
  category: string;
  authorRealName: string;
  replyCount: number;
  solved: boolean;
  createdAt: string;
}

export interface TopicDetail {
  id: number;
  title: string;
  description: string;
  category: string;
  authorRealName: string;
  solved: boolean;
  createdAt: string;
  replies: TopicReply[];
}

export interface TopicReply {
  id: number;
  userId: number;
  userRealName: string;
  content: string;
  isSolution: boolean;
  createdAt: string;
}

export interface TopicQuery {
  page?: number;
  pageSize?: number;
  category?: string;
  keyword?: string;
}
