import { useEffect, useState } from 'react';
import { View, ActivityIndicator } from 'react-native';
import { Redirect } from 'expo-router';
import { useAuthStore } from '../src/store/authStore';
import { instructorApi } from '../src/api/instructor';

export default function Index() {
  const { accessToken, role, isLoading } = useAuthStore();
  const [profileChecked, setProfileChecked] = useState(false);
  const [profileComplete, setProfileComplete] = useState(false);

  useEffect(() => {
    if (isLoading || !accessToken || role !== 'INSTRUCTOR') {
      setProfileChecked(true);
      return;
    }
    instructorApi
      .getMyProfile()
      .then(profile => setProfileComplete(profile.profileComplete))
      .catch(() => setProfileComplete(false))
      .finally(() => setProfileChecked(true));
  }, [isLoading, accessToken, role]);

  if (isLoading || !profileChecked) {
    return (
      <View className="flex-1 items-center justify-center bg-white">
        <ActivityIndicator size="large" color="#2563EB" />
      </View>
    );
  }

  if (!accessToken) {
    return <Redirect href="/(auth)/login" />;
  }

  if (role === 'INSTRUCTOR') {
    return (
      <Redirect href={profileComplete ? '/(instructor)/dashboard' : '/(instructor)/complete-profile'} />
    );
  }

  // LEARNER — stub for now
  return <Redirect href="/(instructor)/dashboard" />;
}
