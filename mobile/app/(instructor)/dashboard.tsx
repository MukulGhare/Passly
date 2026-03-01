import { View, Text } from 'react-native';

// TODO: Show upcoming bookings, today's sessions, quick stats
export default function DashboardScreen() {
  return (
    <View className="flex-1 bg-gray-50 p-4">
      <Text className="text-2xl font-bold text-gray-900">Dashboard</Text>
      <Text className="mt-1 text-gray-500">Your upcoming sessions</Text>
    </View>
  );
}
