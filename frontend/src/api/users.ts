import apiClient from './client';
import type { User } from '../types';

export async function getMe(): Promise<User> {
  const { data } = await apiClient.get<User>('/users/me');
  return data;
}

export async function getAvailableDrivers(): Promise<User[]> {
  const { data } = await apiClient.get<User[]>('/drivers/available');
  return data;
}