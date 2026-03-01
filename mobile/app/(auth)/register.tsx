import { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { router, Link } from 'expo-router';
import { authApi } from '../../src/api/auth';
import { useAuthStore } from '../../src/store/authStore';
import axios from 'axios';

const schema = z.object({
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
});

type FormData = z.infer<typeof schema>;
type Role = 'INSTRUCTOR' | 'LEARNER';

export default function RegisterScreen() {
  const [role, setRole] = useState<Role | null>(null);
  const [serverError, setServerError] = useState('');
  const setAuth = useAuthStore(s => s.setAuth);

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: FormData) => {
    if (!role) return;
    setServerError('');
    try {
      const response = await authApi.register({ ...data, role });
      await setAuth(response);
      router.replace(
        role === 'INSTRUCTOR'
          ? { pathname: '/(instructor)/complete-profile', params: { onboarding: '1' } }
          : '/(instructor)/dashboard'
      );
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setServerError(err.response?.data?.detail ?? 'Registration failed. Please try again.');
      } else {
        setServerError('Something went wrong. Please try again.');
      }
    }
  };

  return (
    <KeyboardAvoidingView
      className="flex-1 bg-white"
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView
        contentContainerStyle={{ flexGrow: 1 }}
        keyboardShouldPersistTaps="handled"
      >
        <View className="flex-1 justify-center px-6 py-10">
          <Text className="text-3xl font-bold text-gray-900 mb-1">Create account</Text>
          <Text className="text-gray-500 mb-8">Join Passly today</Text>

          {serverError ? (
            <View className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 mb-4">
              <Text className="text-red-700 text-sm">{serverError}</Text>
            </View>
          ) : null}

          {/* Role selection */}
          <Text className="text-sm font-medium text-gray-700 mb-2">I am a...</Text>
          <View className="flex-row gap-3 mb-6">
            {(['INSTRUCTOR', 'LEARNER'] as Role[]).map(r => (
              <TouchableOpacity
                key={r}
                onPress={() => setRole(r)}
                className={`flex-1 border-2 rounded-xl py-4 items-center ${
                  role === r ? 'border-blue-600 bg-blue-50' : 'border-gray-200'
                }`}
              >
                <Text className="text-lg mb-1">{r === 'INSTRUCTOR' ? '🧑‍🏫' : '🚗'}</Text>
                <Text className={`font-semibold text-sm ${role === r ? 'text-blue-700' : 'text-gray-700'}`}>
                  {r === 'INSTRUCTOR' ? 'Instructor' : 'Learner'}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          {/* Name row */}
          <View className="flex-row gap-3">
            <View className="flex-1">
              <Text className="text-sm font-medium text-gray-700 mb-1">First name</Text>
              <Controller
                control={control}
                name="firstName"
                render={({ field: { onChange, onBlur, value } }) => (
                  <TextInput
                    className={`border rounded-lg px-4 py-3 text-gray-900 ${
                      errors.firstName ? 'border-red-400' : 'border-gray-300'
                    }`}
                    placeholder="Jane"
                    onBlur={onBlur}
                    onChangeText={onChange}
                    value={value}
                  />
                )}
              />
              {errors.firstName && (
                <Text className="text-red-500 text-xs mt-1">{errors.firstName.message}</Text>
              )}
            </View>
            <View className="flex-1">
              <Text className="text-sm font-medium text-gray-700 mb-1">Last name</Text>
              <Controller
                control={control}
                name="lastName"
                render={({ field: { onChange, onBlur, value } }) => (
                  <TextInput
                    className={`border rounded-lg px-4 py-3 text-gray-900 ${
                      errors.lastName ? 'border-red-400' : 'border-gray-300'
                    }`}
                    placeholder="Smith"
                    onBlur={onBlur}
                    onChangeText={onChange}
                    value={value}
                  />
                )}
              />
              {errors.lastName && (
                <Text className="text-red-500 text-xs mt-1">{errors.lastName.message}</Text>
              )}
            </View>
          </View>

          <Text className="text-sm font-medium text-gray-700 mb-1 mt-4">Email</Text>
          <Controller
            control={control}
            name="email"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                className={`border rounded-lg px-4 py-3 text-gray-900 ${
                  errors.email ? 'border-red-400' : 'border-gray-300'
                }`}
                placeholder="you@example.com"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.email && (
            <Text className="text-red-500 text-xs mt-1 mb-2">{errors.email.message}</Text>
          )}

          <Text className="text-sm font-medium text-gray-700 mb-1 mt-4">Password</Text>
          <Controller
            control={control}
            name="password"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                className={`border rounded-lg px-4 py-3 text-gray-900 ${
                  errors.password ? 'border-red-400' : 'border-gray-300'
                }`}
                placeholder="Min. 8 characters"
                secureTextEntry
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.password && (
            <Text className="text-red-500 text-xs mt-1">{errors.password.message}</Text>
          )}

          <TouchableOpacity
            className={`bg-blue-600 rounded-lg py-4 mt-6 items-center ${
              !role || isSubmitting ? 'opacity-60' : ''
            }`}
            onPress={handleSubmit(onSubmit)}
            disabled={!role || isSubmitting}
          >
            {isSubmitting ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text className="text-white font-semibold text-base">Create Account</Text>
            )}
          </TouchableOpacity>

          {!role && (
            <Text className="text-center text-amber-600 text-xs mt-2">
              Please select a role above to continue
            </Text>
          )}

          <View className="flex-row justify-center mt-6">
            <Text className="text-gray-500">Already have an account? </Text>
            <Link href="/(auth)/login" asChild>
              <TouchableOpacity>
                <Text className="text-blue-600 font-medium">Sign in</Text>
              </TouchableOpacity>
            </Link>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
