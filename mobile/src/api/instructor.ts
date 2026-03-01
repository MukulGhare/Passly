import { apiClient } from './client';

export interface InstructorProfileResponse {
  id: string;
  userId: string;
  firstName: string;
  lastName: string;
  bio: string | null;
  photoUrl: string | null;
  phoneNumber: string | null;
  ratePerHour: number | null;
  yearsExperience: number | null;
  licenseNumber: string | null;
  serviceRadiusKm: number | null;
  latitude: number | null;
  longitude: number | null;
  city: string | null;
  postcode: string | null;
  profileComplete: boolean;
  createdAt: string;
}

export interface UpdateProfileRequest {
  bio?: string;
  phoneNumber?: string;
  ratePerHour?: number;
  yearsExperience?: number;
  licenseNumber?: string;
  serviceRadiusKm?: number;
  city?: string;
  postcode?: string;
}

export const instructorApi = {
  getMyProfile: () =>
    apiClient.get<InstructorProfileResponse>('/api/instructor/profile').then(r => r.data),

  updateProfile: (request: UpdateProfileRequest) =>
    apiClient.put<InstructorProfileResponse>('/api/instructor/profile', request).then(r => r.data),
};
