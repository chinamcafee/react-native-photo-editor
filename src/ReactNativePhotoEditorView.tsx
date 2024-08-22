import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ReactNativePhotoEditorViewProps } from './ReactNativePhotoEditor.types';

const NativeView: React.ComponentType<ReactNativePhotoEditorViewProps> =
  requireNativeViewManager('ReactNativePhotoEditor');

export default function ReactNativePhotoEditorView(props: ReactNativePhotoEditorViewProps) {
  return <NativeView {...props} />;
}
