![expo-brownfield-target by Software Mansion](https://github.com/user-attachments/assets/7994328f-6401-474a-965d-35829ba4ac41)

> [!WARNING]  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

# expo-brownfield-target

`expo-brownfield-target` is an Expo config plugin that allows you to easily extend your Expo app with additional native targets, enabling you to build and distribute it as a brownfield project.

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
  - [Navigation](#navigation)
    - [Methods](#nav-methods)
- [CLI reference](#cli)
  - [CLI commands](#cli-commands)
- [Configuration reference](#configuration)
  - [Android](#configuration-android)
  - [iOS](#configuration-ios)
  - [File templates](#file-templates)
- [Acknowledgments](#acknowledgments)

<a name="motivation"></a>
## Motivation

Brownfield approach enables integrating React Native apps into native Android and iOS projects, but setting it up, especially in Expo projects using Continuous Native Generation is a manual, repetitive and pretty complex task. This plugin aims to fully automate this process on every prebuild and provides a set of configurable file templates and a CLI which streamlines brownfield distribution. Additionally such setup of brownfield allows for easy packaging it as a fat-AAR, XCFramework or Swift Package which simplifies its shipping and enables e.g. simple and more independent cooperation of native and RN teams.

<a name="features"></a>
## Features

- Automatically adds native projects for building brownfield to your Expo app during prebuilds
- Enables easy integration with the Expo project via config plugin interface
- Enables building the brownfield as an XCFramework or an AAR which simplifies usage in the native projects
- Customizable through file templates and the config plugin options
- Supports navigating out of React Native view

**Note:** Our goal is maximum customizability, so if you feel like anything else needs to be customizable, please feel free to cut an issue.

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


<a name="manual-setup"></a>
### Manual setup

All steps performed by the plugin can also be performed manually. Please refer to [MANUAL-SETUP.MD](./MANUAL-SETUP.md) for a full guide for manual setup.

<a name="generating-brownfield-targets"></a>
### Adding brownfield targets

The additional targets for brownfield will be added automatically every time you prebuild the native projects:

```sh
npx expo prebuild --clean
```

<a name="with-cli"></a>
### Building with CLI

The plugin comes with a built-in CLI which can be used to build both Android and iOS targets:

```sh
npx expo-brownfield-target build-android
npx expo-brownfield-target build-ios
```

More details and full reference of the CLI commands can be found below in the [CLI Reference](#cli) section.

<a name="with-manually"></a>
### Building manually

Brownfields can be also built manually using the `xcodebuild` and `./gradlew` commands. Please see [build-xcframework.sh](#./example/scripts/build-xcframework.sh) and [build-aar.sh](#./example/scripts/build-aar.sh) for an example reference of manual building.

<a name="using-built-artifacts"></a>
### Using built artifacts in native projects

Below snippets are taken from the examples of using brownfields inside native apps at: [/examples/android](./example/android/), [/examples/ios](./example/ios/) and [/examples/ios-swiftui](./example/ios-swiftui/).

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

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showReactNativeFragment()
    }

    override fun invokeDefaultOnBackPressed() {
        ...
    }
}
```

<a name="using-swiftui"></a>
### iOS (SwiftUI)

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
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool{
        ReactNativeHostManager.shared.initialize()

        window = UIWindow(frame: UIScreen.main.bounds)
        let viewController = ReactNativeViewController(moduleName: "main")
        window?.rootViewController = viewController
        window?.makeKeyAndVisible()
        
        return true
    }
}
```

<a name="navigation"></a>
## Navigation

<a name="nav-methods"></a>
### Methods

```
popToNative(animated?: boolean)
```

**Description:** A method to return to the native view which precedes the React Native brownfield in the navigation history.

**Arguments:**

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `animated` | No | Specifies if the return to the native view should be performed with an animation | iOS (UIKit) | `false` |

**Example:**

```
import * as ExpoBrownfieldModule from 'expo-brownfield-target';

...

<Button 
  title="Go back" 
  onPress={() => ExpoBrownfieldModule.popToNative()} 
/>

<Button 
  title="Go back animated" 
  onPress={() => ExpoBrownfieldModule.popToNative(true)} 
/>

```

<a name="cli"></a>
## CLI reference

<a name="cli-commands"></a>
### CLI commands

#### `build-android`

Builds the Android library as a fat-AAR and copies it to the artifacts directory. By default it also publishes the AAR to local Maven repository.

```
npx expo-brownfield-target build-android [options]
```

| Option | Short option | Description | Default value |
| --- | --- | --- | --- |
| --help | -h | Displays help message for `build-android` | - |
| --no-publish | - | Skips publishing the AAR to local Maven repo | - |
| --tasks | -t | Enables running specified custom tasks sequentially after `assembleDebug`/`assembleRelease` (e.g. for custom Maven publishing flow). List should be specified in the following format: `task1,task2,task3,task4` | - |
| --debug | -d | Specifies to build the framework with **Debug** configuration. If both `--debug` and `--release` are passed `--release` takes precedence | If no option for configuration is passed framework will be built in **Release** |
| --release | -r | Specifies to build the framework with **Release** configuration. If both options are passed `--release` takes precedence over `--debug` | If no option for configuration is passed framework will be built in **Release** |
| --verbose | - | Output of all commands ran by the CLI (e.g. `./gradlew assembleRelease`) will be printed in the terminal | - |
| --artifacts | -a | Directory where built artifacts (XCFrameworks and AAR) should be placed | `./artifacts` (relative to the Expo project root) |
| --library | -l | The name of the Android library for brownfield | `brownfield` |

#### `build-ios`

Builds the iOS framework, packages it as an XCFramework and places it in the artifacts directory (`artifacts/`) along with the `hermes.xcframework` copied from Pods.

```
npx expo-brownfield-target build-ios [options]
```

| Option | Short option | Description | Default value |
| --- | --- | --- | --- |
| --help | -h | Displays help message for `build-ios` | - |
| --scheme | -s | Scheme for brownfield target which should be build | Scheme name automatically inferred from the native project |
| --xcworkspace | -x | Path to **.xcworkspace** file | Path automatically inferred from the native project |
| --debug | -d | Specifies to build the framework with **Debug** configuration. If both `--debug` and `--release` are passed `--release` takes precedence | If no option for configuration is passed framework will be built in **Release** |
| --release | -r | Specifies to build the framework with **Release** configuration. If both options are passed `--release` takes precedence over `--debug` | If no option for configuration is passed framework will be built in **Release** |
| --verbose | - | Output of all commands ran by the CLI (e.g. `./gradlew assembleRelease`) will be printed in the terminal | - |
| --artifacts | -a | Directory where built artifacts (XCFrameworks and AAR) should be placed | `./artifacts` (relative to the Expo project root) |

<a name="configuration"></a>
## Configuration reference 

<a name="configuration-android"></a>
### Android

| Property | Description | Default value |
| --- | --- | --- |
| `library` | Name of the Android library used for the brownfield | `brownfield` |
| `package` | Package identifier for the brownfield library | `android.package` appended with `.brownfield` or `com.example.brownfield` if `android.package` is undefined  |

<a name="configuration-ios"></a>
### iOS

| Property | Description | Default value |
| --- | --- | --- |
| `bundleIdentifier` | Bundle identifier for the brownfield native target. | `ios.bundleIdentifier` with last component replaced with the target name or `com.example.<target-name>` if `ios.bundleIdentifier` is undefined. |
| `targetName` | Name of the brownfield native target. Also used as the name of the directory containing brownfield files. The value is sanitized to only contain alphanumeric characters and start with a letter. | `config.scheme` or `config.ios.scheme` appended with `brownfield`, if either value is defined and a single string. If not defaults to to `<slug>brownfield`, where `<slug>` is sanitized slug from the Expo project config |

<a name="file-templates"></a>
### File templates

You can also overwrite the templates which are used to generate the files to even better suit the plugin behavior to your requirements. More information about overwriting the templates can be found in [TEMPLATES.md](./TEMPLATES.md).

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
