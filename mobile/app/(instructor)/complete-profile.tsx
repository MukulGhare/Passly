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
import { router, useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { instructorApi } from '../../src/api/instructor';
import { useAuthStore } from '../../src/store/authStore';
import axios from 'axios';

const schema = z.object({
  bio: z.string().min(10, 'Tell learners a bit about yourself (min 10 chars)').max(1000),
  phoneNumber: z.string().min(7, 'Enter a valid phone number').max(20),
  ratePerHour: z
    .string()
    .min(1, 'Required')
    .refine(v => !isNaN(Number(v)) && Number(v) >= 5 && Number(v) <= 500, {
      message: 'Rate must be between £5 and £500',
    }),
  yearsExperience: z
    .string()
    .optional()
    .refine(v => !v || (!isNaN(Number(v)) && Number(v) >= 0 && Number(v) <= 50), {
      message: 'Must be 0–50',
    }),
  licenseNumber: z.string().max(50).optional(),
  serviceRadiusKm: z
    .string()
    .optional()
    .refine(v => !v || (!isNaN(Number(v)) && Number(v) >= 1 && Number(v) <= 100), {
      message: 'Must be 1–100 km',
    }),
  city: z.string().min(1, 'City is required').max(100),
  postcode: z.string().max(20).optional(),
});

type FormData = z.infer<typeof schema>;

function Field({
  label,
  required,
  error,
  children,
}: {
  label: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <View className="mb-4">
      <Text className="text-sm font-medium text-gray-700 mb-1">
        {label}
        {required && <Text className="text-red-500"> *</Text>}
      </Text>
      {children}
      {error && <Text className="text-red-500 text-xs mt-1">{error}</Text>}
    </View>
  );
}

export default function CompleteProfileScreen() {
  const { onboarding } = useLocalSearchParams<{ onboarding?: string }>();
  const isOnboarding = onboarding === '1';
  const isAuthLoading = useAuthStore(s => s.isLoading);
  const [serverError, setServerError] = useState('');
  const [fetchingProfile, setFetchingProfile] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  // Load existing profile data to pre-populate the form
  useEffect(() => {
    if (isAuthLoading) return;
    instructorApi
      .getMyProfile()
      .then(profile => {
        reset({
          bio: profile.bio ?? '',
          phoneNumber: profile.phoneNumber ?? '',
          ratePerHour: profile.ratePerHour != null ? String(profile.ratePerHour) : '',
          yearsExperience: profile.yearsExperience != null ? String(profile.yearsExperience) : '',
          licenseNumber: profile.licenseNumber ?? '',
          serviceRadiusKm: profile.serviceRadiusKm != null ? String(profile.serviceRadiusKm) : '',
          city: profile.city ?? '',
          postcode: profile.postcode ?? '',
        });
      })
      .catch(() => {})
      .finally(() => setFetchingProfile(false));
  }, [isAuthLoading]);

  const onSubmit = async (data: FormData) => {
    setServerError('');
    try {
      await instructorApi.updateProfile({
        bio: data.bio,
        phoneNumber: data.phoneNumber,
        ratePerHour: Number(data.ratePerHour),
        yearsExperience: data.yearsExperience ? Number(data.yearsExperience) : undefined,
        licenseNumber: data.licenseNumber || undefined,
        serviceRadiusKm: data.serviceRadiusKm ? Number(data.serviceRadiusKm) : undefined,
        city: data.city,
        postcode: data.postcode || undefined,
      });
      router.replace(isOnboarding ? '/(instructor)/dashboard' : '/(instructor)/profile');
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setServerError(err.response?.data?.detail ?? 'Failed to save profile. Please try again.');
      } else {
        setServerError('Something went wrong. Please try again.');
      }
    }
  };

  if (isAuthLoading || fetchingProfile) {
    return (
      <View className="flex-1 items-center justify-center bg-white">
        <ActivityIndicator size="large" color="#2563EB" />
      </View>
    );
  }

  return (
    <KeyboardAvoidingView
      className="flex-1 bg-white"
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView
        contentContainerStyle={{ flexGrow: 1 }}
        keyboardShouldPersistTaps="handled"
      >
        <View className="px-6 py-10">
          <Text className="text-3xl font-bold text-gray-900 mb-1">Your profile</Text>
          <Text className="text-gray-500 mb-8">
            Fields marked <Text className="text-red-500">*</Text> are required to appear in search.
          </Text>

          {serverError ? (
            <View className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 mb-6">
              <Text className="text-red-700 text-sm">{serverError}</Text>
            </View>
          ) : null}

          <Field label="Bio" required error={errors.bio?.message}>
            <Controller
              control={control}
              name="bio"
              render={({ field: { onChange, onBlur, value } }) => (
                <TextInput
                  className={`border rounded-lg px-4 py-3 text-gray-900 ${
                    errors.bio ? 'border-red-400' : 'border-gray-300'
                  }`}
                  placeholder="Tell learners about your teaching style and experience..."
                  multiline
                  numberOfLines={4}
                  style={{ minHeight: 100, textAlignVertical: 'top' }}
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                />
              )}
            />
          </Field>

          <Field label="Phone number" required error={errors.phoneNumber?.message}>
            <Controller
              control={control}
              name="phoneNumber"
              render={({ field: { onChange, onBlur, value } }) => (
                <TextInput
                  className={`border rounded-lg px-4 py-3 text-gray-900 ${
                    errors.phoneNumber ? 'border-red-400' : 'border-gray-300'
                  }`}
                  placeholder="+44 7700 900000"
                  keyboardType="phone-pad"
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                />
              )}
            />
          </Field>

          <View className="flex-row gap-3">
            <View className="flex-1">
              <Field label="Rate per hour (£)" required error={errors.ratePerHour?.message}>
                <Controller
                  control={control}
                  name="ratePerHour"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.ratePerHour ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="35"
                      keyboardType="decimal-pad"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
            <View className="flex-1">
              <Field label="Years experience" error={errors.yearsExperience?.message}>
                <Controller
                  control={control}
                  name="yearsExperience"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.yearsExperience ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="5"
                      keyboardType="number-pad"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
          </View>

          <View className="flex-row gap-3">
            <View className="flex-1">
              <Field label="City" required error={errors.city?.message}>
                <Controller
                  control={control}
                  name="city"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.city ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="London"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
            <View className="flex-1">
              <Field label="Postcode" error={errors.postcode?.message}>
                <Controller
                  control={control}
                  name="postcode"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.postcode ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="SW1A 1AA"
                      autoCapitalize="characters"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
          </View>

          <View className="flex-row gap-3">
            <View className="flex-1">
              <Field label="License number" error={errors.licenseNumber?.message}>
                <Controller
                  control={control}
                  name="licenseNumber"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.licenseNumber ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="ADI123456"
                      autoCapitalize="characters"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
            <View className="flex-1">
              <Field label="Service radius (km)" error={errors.serviceRadiusKm?.message}>
                <Controller
                  control={control}
                  name="serviceRadiusKm"
                  render={({ field: { onChange, onBlur, value } }) => (
                    <TextInput
                      className={`border rounded-lg px-4 py-3 text-gray-900 ${
                        errors.serviceRadiusKm ? 'border-red-400' : 'border-gray-300'
                      }`}
                      placeholder="10"
                      keyboardType="number-pad"
                      onBlur={onBlur}
                      onChangeText={onChange}
                      value={value}
                    />
                  )}
                />
              </Field>
            </View>
          </View>

          <TouchableOpacity
            className={`bg-blue-600 rounded-lg py-4 mt-2 items-center ${
              isSubmitting ? 'opacity-60' : ''
            }`}
            onPress={handleSubmit(onSubmit)}
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text className="text-white font-semibold text-base">Save</Text>
            )}
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
