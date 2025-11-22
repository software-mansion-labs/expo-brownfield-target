# File Templates

### Table of contents
- [Overview](#overview)
- [Variable interpolation](#interpolation)
- [Overwriting templates](#overwriting)
- [Android](#android)
  - [Templates](#android-templates)
  - [Variables](#android-variables)
- [iOS](#ios)
  - [Templates](#ios-templates)
  - [Variables](#ios-variables)

<a name="overview"></a>
## Overview

Various files, including the app entrypoints, build configurations or some other configuration files are created based on the templates from 

<a name="interpolation"></a>
## Variable interpolation

Some of the values used by the file templates cannot be predefined as they depend on the plugin configuration resolved during the prebuild and are interpolated during the file generation. Interpolated variables are prefixed by `${{` and end with `}}`, everything between the characters is a case-sensitive variable name e.g.:

```
// Expects variable `packageId` to be present during file generation
package ${{packageId}}
```

If you're overwriting any of the templates please pay attention if the default template for the file uses any of the inerpolated values. Variables used in each template and their references can be found in the tables below

<a name="overwriting"></a>
## Overwriting templates

To overwrite any of the templates listed in the below tables create a `.brownfield-templates` directory at the root of your Expo project and make sure it includes the templates which you want to overwrite. The names of the overwritten templates have to exactly match the names of the original templates.

Note: `.brownfield-templates` supports both flat and per-platform directory structure:

```
# Flat structure
.brownfield-templates/
|__ ExpoApp.swift
|__ build.gradle.kts

# Per-platform structure
.brownfield-templates/
|__ ios/
    |__ ExpoApp.swift
|__ android/
    |__ build.gradle.kts
```

<a name="android"></a>
## Android

<a name="android-templates"></a>
### Templates

| File | Default template | Description | Interpolated variable |
| --- | --- | --- | --- |
| AndroidManifest.xml | [AndroidManifest.xml](./plugin/templates/android/AndroidManifest.xml) | Simple Android manifest for the brownfield library | - |
| build.gradle.kts | [build.gradle.kts](./plugin/templates/android/build.gradle.kts) | Gradle build settings for the brownfield library | `${{packageId}}`, `${{groupId}}`, `${{artifactId}}` |
| consumer-rules.pro | [consumer-rules.pro](./plugin/templates/android/consumer-rules.pro) | Empty consumer-rules file | - |
| proguard-rules.pro | [proguard-rules.pro](./plugin/templates/android/proguard-rules.pro) | Default set of Proguard rules for the brownfield library | - |
| ReactNativeHostManager.kt | [ReactNativeHostManager.kt](./plugin/templates/android/ReactNativeHostManager.kt) | React Native host manager used to initialize and intergrate RN app with the native app lifecycle | `${{packageId}}` |
| ReactNativeViewFactory.kt | [ReactNativeViewFactory.kt](./plugin/templates/android/ReactNativeViewFactory.kt) | React Native view factory used to create views hosting the React Native app | `${{packageId}}` |
| ReactNativeFragment.kt | [ReactNativeFragment.kt](./plugin/templates/android/ReactNativeFragment.kt) | Android fragment used to display the React Native app | `${{packageId}}` |

<a name="android-variables"></a>
### Variables

| Variable | Description | Example value |
| --- | --- | --- |
| packageId | Java/Kotlin package identifier. Aligned with the directory structure of the brownfield library | `com.swmansion.brownfield-project.brownfield` |
| groupId | Package identifier stripped of the last component. Used for publihsing artifacts to Maven | `com.swmansion.brownfield-project` |
| artifactId | Artifact identifier. Last component of the package identifier. Used for publihsing artifacts to Maven | `brownfield` |

<a name="ios"></a>
## iOS

<a name="ios-templates"></a>
### Templates

| File | Default template | Description | Interpolated variable |
| --- | --- | --- | --- |
| ExpoApp.swift | [ExpoApp.swift](./plugin/templates/ios/ExpoApp.swift) | React Native host manager used to initialize and integrate RN app with the native app lifecycle. Also used for loading the view hosting the React Native app | - |
| Info.plist | [Info.plist](./plugin/templates/ios/Info.plist) | Contains native target metadata and settings. Required by Xcode | `${{bundleIdentifier}}`, `${{targetName}}` |
| patch-expo.sh | [patch-expo.sh](./plugin/templates/ios/patch-expo.sh) | Script embedded within the build steps of the brownfield target. Used to patch Expo Modules | `${{projectName}}`, `${{targetName}}` |
| ReactNativeView.swift | [ReactNativeView.swift](./plugin/templates/ios/ReactNativeView.swift) | Brownfield entrypoint for SwiftUI apps | - |
| <target-name>.entitlements | [Target.entitlements](./plugin/templates/ios/Target.entitlements) | The `.entitlements` configuration file for the brownfield target. The name is dynamically set to match the brownfield target name | - |
| ReactNativeLoader.m | [ReactNativeLoader.m](./plugin/templates/ios/ReactNativeLoader.m) | Automatically initializes React Native host manager on application load | - |

<a name="ios-variables"></a>
### Variables

| Variable | Description | Example value |
| --- | --- | --- |
| bundleIdentifier | Bundle identifier of the native target for brownfield | `com.swmansion.brownfield-project.brownfield` |
| targetName | The name of the native target, scheme and directory for the brownfield framework | `MyBrownfield` |
| projectName | The name of the native project | `brownfieldproject` |
