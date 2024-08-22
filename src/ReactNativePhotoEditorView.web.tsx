import * as React from 'react';

import { ReactNativePhotoEditorViewProps } from './ReactNativePhotoEditor.types';

export default function ReactNativePhotoEditorView(props: ReactNativePhotoEditorViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
