import axios from 'axios';

/**
 * Base URL for the Spring Boot backend.
 *
 * - iOS Simulator / web browser: localhost works fine
 * - Android Emulator: replace with 10.0.2.2
 * - Physical device (Expo Go): replace with your machine's LAN IP, e.g. 192.168.1.x
 */
const API_BASE_URL = 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach Bearer token to every request (populated by authStore after login)
apiClient.interceptors.request.use(config => {
  // Import lazily to avoid circular dependency
  const { useAuthStore } = require('../store/authStore');
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
