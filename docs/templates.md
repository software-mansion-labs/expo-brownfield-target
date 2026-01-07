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

<a id="overview"></a>
## Overview

Various files, including the app entrypoints, build configurations or some other configuration files are created based on the templates from [templates directory](../plugin/src/templates/).

<a id="interpolation"></a>
## Variable interpolation

Some of the values used by the file templates cannot be predefined as they depend on the plugin configuration resolved during the prebuild and are interpolated during the file generation. Interpolated variables are prefixed by `${{` and end with `}}`, everything between these characters is a case-sensitive variable name e.g.:

```
// Expects variable `packageId` to be present during file generation
package ${{packageId}}
```

If you're overwriting any of the templates please pay attention if the default template for the file uses any interpolated values. Variables used in each template and their references can be found in the tables below.

<a id="overwriting"></a>
## Overwriting templates

To overwrite any of the templates listed in the below tables create a `.brownfield-templates` directory at the root of your Expo project and make sure it includes the templates which you want to overwrite. The names of the overwritten templates have to exactly match the names of the original templates.

Note: `.brownfield-templates` supports both flat and per-platform directory structure:

```
# Flat structure
.brownfield-templates/
|__ ReactNativeHostManager.swift
|__ build.gradle.kts

# Per-platform structure
.brownfield-templates/
|__ ios/
    |__ ReactNativeHostManager.swift
|__ android/
    |__ build.gradle.kts
```

<a id="android"></a>
## Android

<a id="android-templates"></a>
### Templates

| File | Default template | Description | Interpolated variable |
| --- | --- | --- | --- |
| AndroidManifest.xml | [AndroidManifest.xml](../plugin/src/templates/android/AndroidManifest.xml) | Simple Android manifest for the brownfield library | - |
| build.gradle.kts | [build.gradle.kts](../plugin/src/templates/android/build.gradle.kts) | Gradle build settings for the brownfield library | `${{packageId}}`, `${{groupId}}`, `${{version}}` |
| consumer-rules.pro | [consumer-rules.pro](../plugin/src/templates/android/consumer-rules.pro) | Empty consumer-rules file | - |
| proguard-rules.pro | [proguard-rules.pro](../plugin/src/templates/android/proguard-rules.pro) | Default set of Proguard rules for the brownfield library | - |
| ReactNativeHostManager.kt | [ReactNativeHostManager.kt](../plugin/src/templates/android/ReactNativeHostManager.kt) | React Native host manager used to initialize and integrate RN app with the native app lifecycle | `${{packageId}}` |
| ReactNativeViewFactory.kt | [ReactNativeViewFactory.kt](../plugin/src/templates/android/ReactNativeViewFactory.kt) | React Native view factory used to create views hosting the React Native app | `${{packageId}}` |
| ReactNativeFragment.kt | [ReactNativeFragment.kt](../plugin/src/templates/android/ReactNativeFragment.kt) | Android fragment used to display the React Native app | `${{packageId}}` |

<a id="android-variables"></a>
### Variables

| Variable | Description | Example value |
| --- | --- | --- |
| packageId | Java/Kotlin package identifier. Aligned with the directory structure of the brownfield library | `com.swmansion.brownfield-project.brownfield` |
| groupId | Package identifier stripped of the last component. Used for publishing artifacts to Maven | `com.swmansion.brownfield-project` |
| version | Specifies version for the brownfield library. Used for publishing to Maven repositories |

<a id="ios"></a>
## iOS

<a id="ios-templates"></a>
### Templates

| File | Default template | Description | Interpolated variable |
| --- | --- | --- | --- |
| ReactNativeHostManager.swift | [ReactNativeHostManager.swift](../plugin/src/templates/ios/ReactNativeHostManager.swift) | React Native host manager used to initialize and integrate RN app with the native app lifecycle. Also used for loading the view hosting the React Native app | - |
| ReactNativeDelegate.swift | [ReactNativeDelegate.swift](../plugin/src/templates/ios/ReactNativeDelegate.swift) | Expo React Native factory delegate responsible for providing the bundle URL  | - |
| `BrownfieldAppDelegate.swift` | [BrownfieldAppDelegate.swift](../plugin/src/templates/ios/BrownfieldAppDelegate.swift) | UIApplicationDelegate which automatically forwards methods to ExpoAppDelegate and can be used to integrate host app with it | - |
| `ExpoAppDelegateWrapper.swift` | [ExpoAppDelegateWrapper.swift](../plugin/src/templates/ios/ExpoAppDelegateWrapper.swift) | Wrapper which exposes ExpoAppDelegate to the host application | - |
| Messaging.swift | [Messaging.swift](../plugin/src/templates/ios/Messaging.swift) | Exposes bi-directional communication methods | - |
| Info.plist | [Info.plist](../plugin/src/templates/ios/Info.plist) | Contains native target metadata and settings. Required by Xcode | `${{bundleIdentifier}}`, `${{targetName}}` |
| patch-expo.sh | [patch-expo.sh](../plugin/src/templates/ios/patch-expo.sh) | Script embedded within the build steps of the brownfield target. Used to patch Expo Modules | `${{projectName}}`, `${{targetName}}` |
| ReactNativeView.swift | [ReactNativeView.swift](../plugin/src/templates/ios/ReactNativeView.swift) | Brownfield entrypoint for SwiftUI apps | - |
| &lt;target-name&gt;.entitlements | [Target.entitlements](../plugin/src/templates/ios/Target.entitlements) | The `.entitlements` configuration file for the brownfield target. The name is dynamically set to match the brownfield target name | - |
| ReactNativeViewController.swift | [ReactNativeViewController.swift](../plugin/src/templates/ios/ReactNativeViewController.swift) | View controller for rendering React Native app in a UIKit application | - |

<a id="ios-variables"></a>
### Variables

| Variable | Description | Example value |
| --- | --- | --- |
| bundleIdentifier | Bundle identifier of the native target for brownfield | `com.swmansion.brownfield-project.brownfield` |
| targetName | The name of the native target, scheme and directory for the brownfield framework | `MyBrownfield` |
| projectName | The name of the native project | `brownfieldproject` |
