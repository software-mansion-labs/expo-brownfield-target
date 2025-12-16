![expo-brownfield-target by Software Mansion](https://github.com/user-attachments/assets/7994328f-6401-474a-965d-35829ba4ac41)

> [!WARNING]  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

# expo-brownfield-target

`expo-brownfield-target` is an Expo config plugin that allows you to easily extend your Expo app with additional native targets, enabling you to build and distribute it as a brownfield project.

### 📖 Documentation

- [CLI](./docs/cli.md)
- [Communication API](./docs/api.md#communication)
- [Navigation API](./docs/api.md#navigation)

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
- Supports navigating out of React Native view and communication between the brownfield and native apps

**Note:** Our goal is maximum customizability, so if you feel like anything else needs to be customizable, please feel free to cut an issue or a discussion.

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
        // ...
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

<a name="configuration"></a>

## Configuration reference

<a name="configuration-android"></a>

### Android

| Property     | Description                                                                                   | Default value                                                                                               |
| ------------ | --------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `library`    | Name of the Android library used for the brownfield                                           | `brownfield`                                                                                                |
| `package`    | Package identifier for the brownfield library                                                 | `android.package` appended with `.brownfield` or `com.example.brownfield` if `android.package` is undefined |
| `group`      | Group property for the brownfield library                                                     | Resolved value of the `package` stripped of the last component                                              |
| `version`    | Version of the brownfield library                                                             | 1.0.0                                                                                                       |
| `publishing` | An array of Maven publishing configurations. For more detailed reference see the next section | `[{ type: 'localMaven' }]`                                                                                  |

#### Publishing configuration

`publishing` property accepts an array of zero or more repository configuratons of the following types. Each type of repository configuration may be used more than once (though duplicate entries for Maven Local will be merged).

- Maven Local

  Default `mavenLocal()` repository.

  **Type:**

  ```ts
  type LocalMavenPublication = {
    type: 'localMaven';
  };
  ```

  **Example:**

  ```json
  {
    "type": "localDirectory"
  }
  ```

- Custom local directory

  A custom directory path. Can be an absolute path or a relative path (resolved against the Expo project root).

  Name property is optional and is used to define the publishing Gradle tasks. If not passed a default name suffixed with a number will be automatically generated: `localDirectory1`, `localDirectory2`, ...

  **Type:**

  ```ts
  type LocalDirectoryPublication = {
    type: 'localDirectory';
    name?: string;
    path: string;
  };
  ```

  **Example:**

  ```json
  {
    "type": "localDirectory",
    "name": "customLocal",
    "path": "./maven"
  }
  ```

- Public remote repository

  A remote repository without authentication.

  Name property is optional and is used to define the publishing Gradle tasks. If not passed a default name suffixed with a number will be automatically generated: `remotePublic1`, `remotePublic2`, ...

  Accepts optional `allowInsecure` setting which translates to Maven's [isAllowInsecureProtocol](https://docs.gradle.org/current/kotlin-dsl/gradle/org.gradle.api.artifacts.repositories/-url-artifact-repository/is-allow-insecure-protocol.html) and specifies whether it is possible to communicate with a repository via an insecure connection.

  **Type:**

  ```ts
  type RemotePublicPublication = {
    type: 'remotePublic';
    name?: string;
    url: string;
    allowInsecure?: boolean;
  };
  ```

  **Example:**

  ```json
  {
    "type": "remotePublic",
    "name": "remotePublic",
    "url": "http://localhost:8081/repository/remote-public",
    "allowInsecure": true
  }
  ```

- Private remote repository

  A remote repository with password-based authentication.

  Name property is optional and is used to define the publishing Gradle tasks. If not passed a default name suffixed with a number will be automatically generated: `remotePrivate1`, `remotePrivate2`, ...

  Username, password and the URL can be either passed as simple strings or as objects with `variable` property if you want them to be read from the environment variables (which you can pass e.g. by providing an `.env` file at the root of the Expo project).

  > [!WARNING]  
  > If the values are read from environment variables they will be inserted into android project's `build.gradle` file on prebuild. Watch out to not commit the prebuilt native project to GitHub.

  Accepts optional `allowInsecure` setting which translates to Maven's [isAllowInsecureProtocol](https://docs.gradle.org/current/kotlin-dsl/gradle/org.gradle.api.artifacts.repositories/-url-artifact-repository/is-allow-insecure-protocol.html) and specifies whether it is possible to communicate with a repository via an insecure connection.

  **Type:**

  ```ts
  type EnvValue = {
    variable: string;
  };

  type RemotePrivateBasicPublication = {
    type: 'remotePrivate';
    name?: string;
    url: string | EnvValue;
    username: string | EnvValue;
    password: string | EnvValue;
    allowInsecure?: boolean;
  };
  ```

  **Example:**

  ```json
  {
    "type": "remotePrivate",
    "url": {
      "variable": "MAVEN_REPO_URL"
    },
    "username": {
      "variable": "MAVEN_REPO_USERNAME"
    },
    "password": {
      "variable": "MAVEN_REPO_PASSWORD"
    }
  }
  ```

<a name="configuration-ios"></a>

### iOS

| Property           | Description                                                                                                                                                                                       | Default value                                                                                                                                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `bundleIdentifier` | Bundle identifier for the brownfield native target.                                                                                                                                               | `ios.bundleIdentifier` with last component replaced with the target name or `com.example.<target-name>` if `ios.bundleIdentifier` is undefined.                                                                            |
| `targetName`       | Name of the brownfield native target. Also used as the name of the directory containing brownfield files. The value is sanitized to only contain alphanumeric characters and start with a letter. | `config.scheme` or `config.ios.scheme` appended with `brownfield`, if either value is defined and a single string. If not defaults to to `<slug>brownfield`, where `<slug>` is sanitized slug from the Expo project config |

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
