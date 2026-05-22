export interface TopicListItem {
  id: number;
  title: string;
  category: string;
  authorName: string;
  replyCount: number;
  solved: boolean;
  createdAt: string;
}

export interface TopicDetail {
  id: number;
  title: string;
  description: string;
  category: string;
  authorUserId: number;
  authorName: string;
  solved: boolean;
  createdAt: string;
  replies: TopicReply[];
}

export interface TopicReply {
  id: number;
  userId: number;
  userName: string;
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

export interface TopicCreateRequest {
  title: string;
  description?: string;
  category: string;
}

export interface TopicReplyCreateRequest {
  content: string;
}
