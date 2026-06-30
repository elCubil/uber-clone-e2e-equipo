import apiClient from './client';
import type { Trip } from '../types';

export async function createTrip(pickupAddress: string, dropoffAddress: string): Promise<Trip> {
  const { data } = await apiClient.post<Trip>('/trips', { pickupAddress, dropoffAddress });
  return data;
}

export async function getMyTrips(): Promise<Trip[]> {
  const { data } = await apiClient.get<Trip[]>('/trips');
  return data;
}

export async function getPendingTrips(): Promise<Trip[]> {
  const { data } = await apiClient.get<Trip[]>('/trips/pending');
  return data;
}

export async function getDriverTrips(): Promise<Trip[]> {
  const { data } = await apiClient.get<Trip[]>('/trips/my');
  return data;
}

export async function getTripById(id: number): Promise<Trip> {
  const { data } = await apiClient.get<Trip>(`/trips/${id}`);
  return data;
}

export async function acceptTrip(id: number): Promise<Trip> {
  const { data } = await apiClient.patch<Trip>(`/trips/${id}/accept`);
  return data;
}

export async function completeTrip(id: number): Promise<Trip> {
  const { data } = await apiClient.patch<Trip>(`/trips/${id}/complete`);
  return data;
}

export async function rateTrip(id: number, rating: number, comment?: string): Promise<Trip> {
  const { data } = await apiClient.post<Trip>(`/trips/${id}/rate`, { rating, comment });
  return data;
}