import { Page } from '../common.types';

export interface CreateAnswerRequest {
  content: string;
}

export interface AnswerSummary {
  id: number;
  nickname: string;
  content: string;
  accepted: boolean;
  likeCount: number;
  dislikeCount: number;
  myReactionType: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
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
}

export interface UpdateAnswerRequest {
  content: string;
}

export type AnswerPage = Page<AnswerSummary>;

