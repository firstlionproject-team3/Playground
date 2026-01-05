export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
}

export interface OAuthTokenRequest {
  code: string;
}

export interface OAuthTokenResponse {
  accessToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
}

