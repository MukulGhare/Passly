import { create } from 'zustand';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';
import { AuthResponse } from '../api/auth';

// expo-secure-store is native-only; fall back to localStorage on web
const storage = {
  getItem: (key: string): Promise<string | null> => {
    if (Platform.OS === 'web') {
      return Promise.resolve(localStorage.getItem(key));
    }
    return SecureStore.getItemAsync(key);
  },
  setItem: (key: string, value: string): Promise<void> => {
    if (Platform.OS === 'web') {
      localStorage.setItem(key, value);
      return Promise.resolve();
    }
    return SecureStore.setItemAsync(key, value);
  },
  deleteItem: (key: string): Promise<void> => {
    if (Platform.OS === 'web') {
      localStorage.removeItem(key);
      return Promise.resolve();
    }
    return SecureStore.deleteItemAsync(key);
  },
};

const KEYS = {
  accessToken: 'passly_access_token',
  refreshToken: 'passly_refresh_token',
  userId: 'passly_user_id',
  email: 'passly_email',
  role: 'passly_role',
};

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  email: string | null;
  role: string | null;
  /** true while we're reading tokens from SecureStore on app start */
  isLoading: boolean;

  /** Called after a successful login or register */
  setAuth: (response: AuthResponse) => Promise<void>;
  logout: () => Promise<void>;
  /** Read persisted tokens from SecureStore — call once on app start */
  loadFromStorage: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  userId: null,
  email: null,
  role: null,
  isLoading: true,

  setAuth: async (response) => {
    await Promise.all([
      storage.setItem(KEYS.accessToken, response.accessToken),
      storage.setItem(KEYS.refreshToken, response.refreshToken),
      storage.setItem(KEYS.userId, response.userId),
      storage.setItem(KEYS.email, response.email),
      storage.setItem(KEYS.role, response.role),
    ]);
    set({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      userId: response.userId,
      email: response.email,
      role: response.role,
    });
  },

  logout: async () => {
    await Promise.all(Object.values(KEYS).map(k => storage.deleteItem(k)));
    set({ accessToken: null, refreshToken: null, userId: null, email: null, role: null });
  },

  loadFromStorage: async () => {
    const [accessToken, refreshToken, userId, email, role] = await Promise.all([
      storage.getItem(KEYS.accessToken),
      storage.getItem(KEYS.refreshToken),
      storage.getItem(KEYS.userId),
      storage.getItem(KEYS.email),
      storage.getItem(KEYS.role),
    ]);
    set({ accessToken, refreshToken, userId, email, role, isLoading: false });
  },
}));
