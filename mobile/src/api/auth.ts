import { apiClient } from './client';

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: 'INSTRUCTOR' | 'LEARNER';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userId: string;
  email: string;
  role: string;
}

export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<AuthResponse>('/api/auth/register', data).then(r => r.data),

  login: (data: LoginRequest) =>
    apiClient.post<AuthResponse>('/api/auth/login', data).then(r => r.data),
};
