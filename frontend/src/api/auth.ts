import apiClient from './client';
import type { Role } from '../types';

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
}

export interface LoginPayload {
  email: string;
  password: string;
}

interface AuthResponse {
  token: string;
}

export async function register(payload: RegisterPayload): Promise<string> {
  const { data } = await apiClient.post<AuthResponse>('/auth/register', payload);
  return data.token;
}

export async function login(payload: LoginPayload): Promise<string> {
  const { data } = await apiClient.post<AuthResponse>('/auth/login', payload);
  return data.token;
}