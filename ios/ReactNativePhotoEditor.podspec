require 'json'

package = JSON.parse(File.read(File.join(__dir__, '..', 'package.json')))

Pod::Spec.new do |s|
  s.name           = 'ReactNativePhotoEditor'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.homepage       = package['homepage']
  s.platforms      = { :ios => '13.4', :tvos => '13.4' }
  s.swift_version  = '5.4'
  s.source         = { git: 'https://github.com/chinamcafee/react-native-photo-editor' }
  # s.static_framework = true
  s.resource_bundles = {
    'HXPhotoPicker' => ['ReactNativePhotoPickerEditor/HXPhotoPicker/Resources/**']
  }

  s.dependency 'ExpoModulesCore'

  # Swift/Objective-C compatibility
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
    'SWIFT_COMPILATION_MODE' => 'wholemodule',
    'SWIFT_ACTIVE_COMPILATION_CONDITIONS' => 'HXPICKER_ENABLE_PICKER HXPICKER_ENABLE_EDITOR HXPICKER_ENABLE_EDITOR_VIEW HXPICKER_ENABLE_CAMERA_LOCATION HXPICKER_ENABLE_CAMERA'
  }

  s.source_files = "**/*.{h,m,swift}"
end
