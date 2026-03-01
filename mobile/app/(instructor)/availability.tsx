import { View, Text } from 'react-native';

// TODO: Weekly recurring slots + block individual dates
export default function AvailabilityScreen() {
  return (
    <View className="flex-1 bg-gray-50 p-4">
      <Text className="text-2xl font-bold text-gray-900">Availability</Text>
      <Text className="mt-1 text-gray-500">Set your weekly schedule</Text>
    </View>
  );
}
