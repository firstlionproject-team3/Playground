// API Response Types
export interface ApiResponse<T> {
  content?: T[];
  totalElements?: number;
  totalPages?: number;
  size?: number;
  number?: number;
  [key: string]: any;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// User Types
export interface User {
  id: number;
  loginId: string;
  nickname: string;
  email?: string;
  joinedDate?: string;
}

export interface UserRegisterRequest {
  loginId: string;
  password: string;
  email?: string;
}

export interface UserRegisterResponse {
  id: number;
  loginId: string;
  nickname: string;
  email?: string;
}

export interface UserMyPageResponse {
  id?: number;
  nickname: string;
  email?: string;
  joinedDate: string;
  loginId?: string;
  roles?: string[];
}

export interface UserUpdateRequest {
  nickname: string;
  email?: string;
}

// Auth Types
export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
}

// Question Types
export interface QuestionSummary {
  id: number;
  nickname: string;
  title: string;
  createdAt: string;
  answerCount?: number;
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

export interface QuestionResponse {
  id: number;
  userId?: number;
  nickname: string;
  title: string;
  content: string;
  viewCount: number;
  likeCount?: number;
  dislikeCount?: number;
  myReactionType?: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
  updatedAt: string;
  answers: AnswerDetail[];
}

export interface QuestionCreateRequest {
  title: string;
  content: string;
}

export interface QuestionUpdateRequest {
  title?: string;
  content?: string;
}

export interface IdResponse {
  id: number;
}

// Answer Types
export interface AnswerSummary {
  id: number;
  nickname: string;
  content: string;
  accepted: boolean;
  likeCount: number;
  dislikeCount: number;
  myReactionType?: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
}

export interface AnswerDetail {
  id: number;
  userId?: number;
  nickname: string;
  content: string;
  accepted: boolean;
  likeCount: number;
  dislikeCount: number;
  myReactionType?: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
  comments: CommentResponse[];
}

export interface AnswerCreateRequest {
  content: string;
}

export interface AnswerUpdateRequest {
  content: string;
}

// Comment Types
export interface CommentResponse {
  id: number;
  userId?: number;
  nickname: string;
  content: string;
  likeCount?: number;
  dislikeCount?: number;
  myReactionType?: 'LIKE' | 'DISLIKE' | 'NONE';
  createdAt: string;
}

export interface CommentCreateRequest {
  content: string;
}

export interface CommentUpdateRequest {
  content: string;
}

// Reaction Types
export type TargetType = 'QUESTION' | 'ANSWER' | 'COMMENT';
export type ReactionType = 'LIKE' | 'DISLIKE';

export interface ReactionRequest {
  targetType: TargetType;
  targetId: number;
  reactionType: ReactionType;
}

export interface ReactionCount {
  likeCount: number;
  dislikeCount: number;
}

export interface MyReaction {
  hasReaction: boolean;
  reactionType: ReactionType | 'NONE';
}

// Notification Types
export type NotificationType = 
  | 'NEW_ANSWER'
  | 'REPORT_RECEIVED'
  | 'ANSWER_ACCEPTED'
  | 'COMMENT_ADDED';

export interface Notification {
  id: number;
  type: NotificationType;
  content: string;
  senderId: number | null;
  receiverId: number;
  isRead: boolean;
  createdAt: string;
  questionId?: number;
  answerId?: number;
  commentId?: number;
}

export interface NotificationListResponse {
  notifications: Notification[];
  totalCount: number;
}

// Report Types
export type EntityType = 'QUESTION' | 'ANSWER' | 'COMMENT' | 'USER';
export type ReportCategory = 
  | 'SPAM'
  | 'ABUSE'
  | 'INAPPROPRIATE'
  | 'COPYRIGHT'
  | 'MISINFORMATION'
  | 'ETC';

export interface ReportCreateRequest {
  reportedId: number;
  entityType: EntityType;
  entityId: number;
  category: ReportCategory;
  reasonDetail?: string;
}

export interface ReportResponse {
  reportId: number;
  reporterNickname: string;
  reportedNickname: string;
  entityType: EntityType;
  entityId: number;
  reportCategory: ReportCategory;
  detail?: string;
  reportStatus: 'PENDING' | 'APPROVED' | 'REJECTED';
  reportDate: string;
}

// Error Types
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  code: string;
  message: string;
  path: string;
  details?: Record<string, string>;
}

