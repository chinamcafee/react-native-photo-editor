import { StyleSheet, Text, View } from 'react-native';

import * as ReactNativePhotoEditor from 'react-native-photo-editor';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ReactNativePhotoEditor.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
