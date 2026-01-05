export interface RegisterRequest {
  loginId: string;
  email?: string;
  password: string;
}

export interface RegisterResponse {
  id: number;
  loginId: string;
  nickname: string;
  email: string;
}

export interface MyPageResponse {
  nickname: string;
  email: string;
  joinedDate: string;
}

export interface UpdateMyPageRequest {
  nickname: string;
  email: string;
}

export interface User {
  id: number;
  loginId: string;
  nickname: string;
  email: string;
  roles?: string[];
}

