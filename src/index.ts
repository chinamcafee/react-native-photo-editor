import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ReactNativePhotoEditor.web.ts
// and on native platforms to ReactNativePhotoEditor.ts
import ReactNativePhotoEditorModule from './ReactNativePhotoEditorModule';
import ReactNativePhotoEditorView from './ReactNativePhotoEditorView';
import { ChangeEventPayload, ReactNativePhotoEditorViewProps } from './ReactNativePhotoEditor.types';

// Get the native constant value.
export const PI = ReactNativePhotoEditorModule.PI;

export function hello(): string {
  return ReactNativePhotoEditorModule.hello();
}

export async function openEditor(path:string) {
  console.log("openEditor native started =====================")
  return await ReactNativePhotoEditorModule.openEditor(path);
}

export async function openPicker(path:string) {
  console.log("open native picker started =====================")
  return await ReactNativePhotoEditorModule.openPicker();
}

export async function setValueAsync(value: string) {
  return await ReactNativePhotoEditorModule.setValueAsync(value);
}

const emitter = new EventEmitter(ReactNativePhotoEditorModule ?? NativeModulesProxy.ReactNativePhotoEditor);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ReactNativePhotoEditorView, ReactNativePhotoEditorViewProps, ChangeEventPayload };
