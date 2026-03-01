import { useCallback, useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, ActivityIndicator } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { useAuthStore } from '../../src/store/authStore';
import { instructorApi, InstructorProfileResponse } from '../../src/api/instructor';

function Row({ label, value }: { label: string; value: string | number | null | undefined }) {
  if (value === null || value === undefined || value === '') return null;
  return (
    <View className="flex-row py-3 border-b border-gray-100">
      <Text className="text-sm text-gray-500 w-36">{label}</Text>
      <Text className="text-sm text-gray-900 flex-1">{String(value)}</Text>
    </View>
  );
}

export default function ProfileScreen() {
  const email = useAuthStore(s => s.email);
  const logout = useAuthStore(s => s.logout);
  const isAuthLoading = useAuthStore(s => s.isLoading);
  const [profile, setProfile] = useState<InstructorProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useFocusEffect(
    useCallback(() => {
      if (isAuthLoading) return;
      setLoading(true);
      instructorApi
        .getMyProfile()
        .then(setProfile)
        .catch(() => {})
        .finally(() => setLoading(false));
    }, [isAuthLoading])
  );

  const handleLogout = async () => {
    await logout();
    router.replace('/(auth)/login');
  };

  if (loading) {
    return (
      <View className="flex-1 items-center justify-center bg-white">
        <ActivityIndicator size="large" color="#2563EB" />
      </View>
    );
  }

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <View className="bg-white px-6 pt-12 pb-6 border-b border-gray-200">
        <View className="w-16 h-16 rounded-full bg-blue-100 items-center justify-center mb-3">
          <Text className="text-2xl font-bold text-blue-600">
            {profile?.firstName?.[0] ?? '?'}
          </Text>
        </View>
        <Text className="text-xl font-bold text-gray-900">
          {profile?.firstName} {profile?.lastName}
        </Text>
        <Text className="text-sm text-gray-500 mt-0.5">{email}</Text>
        {profile?.profileComplete && (
          <View className="mt-2 self-start bg-green-100 px-3 py-1 rounded-full">
            <Text className="text-xs text-green-700 font-medium">Profile active</Text>
          </View>
        )}
      </View>

      <View className="bg-white mx-4 mt-4 rounded-xl px-4">
        <Row label="Phone" value={profile?.phoneNumber} />
        <Row label="Rate per hour" value={profile?.ratePerHour ? `£${profile.ratePerHour}` : null} />
        <Row label="City" value={profile?.city} />
        <Row label="Postcode" value={profile?.postcode} />
        <Row label="Years experience" value={profile?.yearsExperience} />
        <Row label="License number" value={profile?.licenseNumber} />
        <Row label="Service radius" value={profile?.serviceRadiusKm ? `${profile.serviceRadiusKm} km` : null} />
      </View>

      {profile?.bio && (
        <View className="bg-white mx-4 mt-4 rounded-xl px-4 py-4">
          <Text className="text-sm font-medium text-gray-500 mb-2">Bio</Text>
          <Text className="text-sm text-gray-900 leading-5">{profile.bio}</Text>
        </View>
      )}

      <View className="mx-4 mt-4 mb-8 gap-3">
        <TouchableOpacity
          className="bg-blue-600 rounded-lg py-3 items-center"
          onPress={() => router.push('/(instructor)/complete-profile')}
        >
          <Text className="text-white font-medium">Edit Profile</Text>
        </TouchableOpacity>

        <TouchableOpacity
          className="border border-red-300 rounded-lg py-3 items-center bg-white"
          onPress={handleLogout}
        >
          <Text className="text-red-600 font-medium">Log out</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}
