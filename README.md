![expo-brownfield-target by Software Mansion](https://github.com/user-attachments/assets/7994328f-6401-474a-965d-35829ba4ac41)

> [!WARNING]  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

# expo-brownfield-target

`expo-brownfield-target` is a library which includes an Expo config plugin that automates brownfield setup in the project, CLI for building the brownfield artifacts and built-in APIs for communication and navigation between the apps.

### 📖 Documentation

- [Android Publishing](./docs/publishing.md)
- [CLI](./docs/cli.md)
- [Communication API](./docs/api.md#communication)
- [Configuration](./docs/configuration.md)
- [Manual Setup](./docs/manual-setup.md)
- [Navigation API](./docs/api.md#navigation)
- [Templates](./docs/templates.md)

### Table of contents

- [Motivation](#motivation)
- [Features](#features)
- [Platform & Expo SDK compatibility](#compat)
- [Usage](#usage)
  - [Installation](#installation)
  - [Plugin setup](#plugin-setup)
  - [Manual setup](#manual-setup)
  - [Adding brownfield targets](#generating-brownfield-targets)
  - [Building with CLI](#with-cli)
  - [Building manually](#with-manually)
  - [Using built artifacts in native projects](#using-built-artifacts)
    - [Android](#using-android)
    - [iOS (SwiftUI)](#using-swiftui)
    - [iOS (UIKit)](#using-uikit)
  - [Using with Metro Bundler](#metro)
- [Acknowledgments](#acknowledgments)

<a name="motivation"></a>

## Motivation

Brownfield approach enables integrating React Native apps into native Android and iOS projects, but setting it up, especially in Expo projects using Continuous Native Generation is a manual, repetitive, and pretty complex task. 

This library aims to fully automate and simplify brownfield setup by including a config plugin that configures your project on every prebuild, built-in APIs for common use cases and CLI which builds the brownfield artifacts.

Such setup of brownfield allows for easy publishing to Maven, as XCFramework or using Swift Package Manager which enables e.g. simple and more independent cooperation of native and RN teams.

<a name="features"></a>

## Features

- Automatic extension of native projects with brownfield targets
- Easy integration with Expo project via config plugin interface
- Artifact publishing using XCFramework for iOS and Maven for Android
- Configurability & customizability
- APIs for bi-directional communication and navigation between the apps

<a name="compat"></a>

## Platform & Expo SDK compatibility

The plugin supports both Android and iOS. As of now we only support Expo SDK 54.

<a name="usage"></a>

## Usage

<a name="installation"></a>

### Installation

```sh
npm install expo-brownfield-target
```

<a name="plugin-setup"></a>

### Plugin setup

Add the config plugin to the `"plugins"` section in your `app.json` or `app.config.js` / `app.config.ts`:

```json
{
  "expo": {
    "name": "my-awesome-expo-project",
    ...
    "plugins": [
      ... // Other plugins
      "expo-brownfield-target"
    ]
  }
}
```

If you want to pass any configuration options make sure to add the plugin as an array:

```json
{
  "expo": {
    "name": "my-awesome-expo-project",
    ...
    "plugins": [
      ... // Other plugins
      [
        "expo-brownfield-target",
        {
          "android": {
            ...
          },
          "ios": {
            ...
          }
        }
      ]
    ]
  }
}
```

See [configuration.md](./docs/configuration.md) for full reference of configurable options.

<a name="manual-setup"></a>

### Manual setup

All steps performed by the plugin can also be performed manually. Please refer to [manual-setup.md](./docs/manual-setup.md) for a full guide for manual setup.

<a name="generating-brownfield-targets"></a>

### Adding brownfield targets

The additional targets for brownfield will be added automatically every time you prebuild the native projects:

```sh
npx expo prebuild
```

<a name="with-cli"></a>

### Building with CLI

The plugin comes with a built-in CLI which can be used to build both Android and iOS targets:

```sh
npx expo-brownfield-target build-android -r MavenLocal

npx expo-brownfield-target build-ios
```

More details and full reference of the CLI commands and options can be found in [cli.md](./docs/cli.md).

<a name="with-manually"></a>

### Building manually

Brownfields can be also built manually using the `xcodebuild` and `./gradlew` commands.

``` bash
# Compile the framework
xcodebuild \
    -workspace "ios/myexpoapp.xcworkspace" \
    -scheme "MyBrownfield" \
    -derivedDataPath "ios/build" \
    -destination "generic/platform=iphoneos" \
    -destination "generic/platform=iphonesimulator" \
    -configuration "Release"

# Package it as an XCFramework
xcodebuild \
    -create-xcframework \
    -framework "ios/build/Build/Products/Release-iphoneos/MyBrownfield.framework" \
    -framework "ios/build/Build/Products/Release-iphonesimulator/MyBrownfield.framework" \
    -output "artifacts/MyBrownfield.xcframework"
```

```bash
./gradlew publishBrownfieldAllPublicationToMavenLocal
```

See [publishing.md](./docs/publishing.md#tasks) for more details about the publishing tasks.

<a name="using-built-artifacts"></a>

### Using built artifacts in native projects

Below snippets are taken from the examples of using brownfields inside native apps at: [example/android](./example/android/), [example/ios](./example/ios/) and [example/ios-swiftui](./example/ios-swiftui/).

<a name="using-android"></a>

### Android

```kotlin
// MainActivity.kt
package com.swmansion.example

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.swmansion.brownfield.showReactNativeFragment
import com.swmansion.brownfield.BrownfieldActivity

class MainActivity : BrownfieldActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showReactNativeFragment()
    }

    override fun invokeDefaultOnBackPressed() {
        // ...
    }
}
```

Extending `BrownfieldActivity` enables automatic integration of `onConfigurationChanged` lifecycle event with Expo lifecycle dispatcher. You can also set it up manually using `BrownfieldLifecycleDispatcher`:

```kotlin
// MainActivity.kt
package com.swmansion.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.swmansion.brownfield.showReactNativeFragment
import com.swmansion.brownfield.BrownfieldLifecycleDispatcher

class MainActivity: AppCompatActivity(), {
  // ...

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    BrownfieldLifecycleDispatcher.onConfigurationChanged(this.application, newConfig)
  }
}
```

`BrownfieldLifecycleDispatcher` also includes `onApplicationCreate` method which accepts the application as it's only parameter, but this method shouldn't be called manually, as it's invoked in `ReactNativeHostManager`.

<a name="using-swiftui"></a>

### iOS (SwiftUI)

```swift
// MyApp.swift
import SwiftUI
import MyBrownfieldApp

@main
struct MyApp: App {
  @UIApplicationDelegateAdaptor var delegate: BrownfieldAppDelegate

  var body: some Scene {
    WindowGroup {
      ContentView()
    }
  }
}
```

`BrownfieldAppDelegate` integrates host app with ExpoAppDelegate and initializes the shared instance of ReactNativeHostManager. You can also initialize it manually:

```swift
// ContentView.swift
import SwiftUI
import MyBrownfieldApp

struct ContentView: View {
    init() {
        ReactNativeHostManager.shared.initialize()
    }

    var body: some View {
        VStack {
            ReactNativeView(moduleName: "main")
        }
    }
}
```

<a name="using-uikit"></a>

### iOS (UIKit)

```swift
// AppDelegate.swift
import UIKit
import MyBrownfieldApp

@main
class AppDelegate: BrownfieldAppDelegate {
    var window: UIWindow?

    override func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        super.application(application, didFinishLaunchingWithOptions: launchOptions)

        window = UIWindow(frame: UIScreen.main.bounds)
        let viewController = ReactNativeViewController(moduleName: "main")
        window?.rootViewController = viewController
        window?.makeKeyAndVisible()

        return true
    }
}
```

`BrownfieldAppDelegate` integrates host app with ExpoAppDelegate and initializes the shared instance of ReactNativeHostManager. You can also initialize it manually and control which of the app delegate methods you want to forward:

```swift
// AppDelegate.swift
import UIKit
import MyBrownfieldApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    override func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        ReactNativeHostManager.shared.initialize()
        ReactNativeHostManager.shared.expoAppDelegateWraper?
            .application(application, didFinishLaunchingWithOptions: launchOptions)

        window = UIWindow(frame: UIScreen.main.bounds)
        let viewController = ReactNativeViewController(moduleName: "main")
        window?.rootViewController = viewController
        window?.makeKeyAndVisible()

        return true
    }
}
```

<a name="metro"></a>

### Using with Metro Bundler

Debug builds use bundle hosted by Metro server (hosted over `localhost:8081`) instead of the bundle included in the brownfield framework.

Be sure to start Metro server by running the following command in your Expo project:

```
npm start
```

#### Android

To be able to use Metro create a separate debug-only Manifest with the following contents in your native app which will host the brownfield:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <application
        android:usesCleartextTraffic="true"
        tools:ignore="GoogleAppIndexingWarning"
        tools:replace="android:usesCleartextTraffic"
        tools:targetApi="28" />
</manifest>
```

Then be sure to build and publish the artifacts using either `All` (includes both debug and release) or `Debug` configuration and to use the debug variant in the native app. Also, don't forget to reverse the port 8081 (if necessary):

```
adb reverse tcp:8081 tcp:8081
```

#### iOS

To use Metro server instead of bundle included at the build time, compile the brownfield framework using `Debug` configuration (`-d`/`--debug` flag when using the CLI). `Debug` XCFramework should automatically source the bundle from the Metro server.

<a name="acknowledgments"></a>

### Acknowledgments

Huge thanks to:

- [@hurali97](https://www.github.com/hurali97) for shipping some of the work we built this on

- [@lukmccall](https://www.github.com/lukmccall), [@aleqsio](https://www.github.com/aleqsio) and [@dawidmatyjasik](https://www.github.com/dawidmatyjasik) for research and support during the plugin development

- [@callstack](https://github.com/callstack) for shipping the great set of helpers for brownfields ([react-native-brownfield](https://github.com/callstack/react-native-brownfield)) which inspired parts of this library

## expo-brownfield-target is created by Software Mansion

[![swm](https://logo.swmansion.com/logo?color=white&variant=desktop&width=150&tag=typegpu-github 'Software Mansion')](https://swmansion.com)

Since 2012 [Software Mansion](https://swmansion.com) is a software agency with
experience in building web and mobile apps. We are Core React Native
Contributors and experts in dealing with all kinds of React Native issues. We
can help you build your next dream product –
[Hire us](https://swmansion.com/contact/projects?utm_source=typegpu&utm_medium=readme).

<!-- automd:contributors author="software-mansion" -->

Made by [@software-mansion](https://github.com/software-mansion) and
[community](https://github.com/software-mansion-labs/expo-brownfield-target/graphs/contributors) 💙
<br><br>
<a href="https://github.com/software-mansion-labs/expo-brownfield-target/graphs/contributors">
<img src="https://contrib.rocks/image?repo=software-mansion-labs/expo-brownfield-target" />
</a>

<!-- /automd -->
