import { Tabs } from 'expo-router';

export default function InstructorLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: '#3B82F6',
        headerShown: false,
      }}
    >
      <Tabs.Screen name="dashboard" options={{ title: 'Dashboard' }} />
      <Tabs.Screen name="availability" options={{ title: 'Availability' }} />
      <Tabs.Screen name="students" options={{ title: 'Students' }} />
      <Tabs.Screen name="profile" options={{ title: 'Profile' }} />
      {/* Onboarding screen — hidden from tab bar */}
      <Tabs.Screen name="complete-profile" options={{ href: null }} />
    </Tabs>
  );
}
