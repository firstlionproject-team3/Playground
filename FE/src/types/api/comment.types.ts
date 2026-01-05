export interface CreateCommentRequest {
  content: string;
}

export interface CommentResponse {
  id: number;
  nickname: string;
  content: string;
  createdAt: string;
}

export interface UpdateCommentRequest {
  content: string;
}

