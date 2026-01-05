import { Page } from '../common.types';

export interface CreateQuestionRequest {
  title: string;
  content: string;
}

export interface CreateQuestionResponse {
  id: number;
}

export interface QuestionSummary {
  id: number;
  nickname: string;
  title: string;
  createdAt: string;
}

export interface QuestionDetail {
  id: number;
  nickname: string;
  title: string;
  content: string;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  answers: AnswerDetail[];
}

export interface AnswerDetail {
  id: number;
  nickname: string;
  content: string;
  accepted: boolean;
  likeCount: number;
  dislikeCount: number;
  myReactionType: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
  comments: CommentResponse[];
}

export interface CommentResponse {
  id: number;
  nickname: string;
  content: string;
  createdAt: string;
}

export interface UpdateQuestionRequest {
  title?: string;
  content?: string;
}

export type QuestionPage = Page<QuestionSummary>;

