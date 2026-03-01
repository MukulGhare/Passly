import { View, Text } from 'react-native';

// TODO: List students, progress notes, session history per student
export default function StudentsScreen() {
  return (
    <View className="flex-1 bg-gray-50 p-4">
      <Text className="text-2xl font-bold text-gray-900">Students</Text>
      <Text className="mt-1 text-gray-500">Manage your learners</Text>
    </View>
  );
}
