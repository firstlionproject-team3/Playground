export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
export const OAUTH_REDIRECT_URL = import.meta.env.VITE_OAUTH_REDIRECT_URL || 'http://localhost:3000';

export const PAGE_SIZE = {
  QUESTION: 10,
  ANSWER: 10,
  USER: 20,
  REPORT: 10,
};

export const ADMIN_USER_ID = 5; // 하드코딩된 관리자 ID

