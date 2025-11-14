<!-- <img width="5334" height="2667" alt="expo-brownfield-target-banner" src="https://github.com/user-attachments/assets/7994328f-6401-474a-965d-35829ba4ac41" /> -->

> [!WARNING]  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

# expo-brownfield-target

`expo-brownfield-target` is an Expo config plugin which enables you to effortlessly extend your Expo app with additional native targets for building and shipping it as a brownfield

### Table of contents
- [Features](#features)
- [Platform & Expo SDK compatibility](#compat)
- [Usage](#usage)
  - [Installation](#installation)
  - [Plugin setup](#plugin-setup)
  - [Adding brownfield targets](#generating-brownfield-targets)
  - [Building with CLI](#with-cli)
  - [Building manually](#with-manually)
- [CLI reference](#cli)
  - [CLI commands](#cli-commands)
- [Configuration reference](#configuration)
  - [iOS](#configuration-ios)

<a name="features"></a>
## Features

- Automatically extends native projects in your Expo app with targets for building brownfield during each prebuild
- Simple integration with the Expo project and customization via the config plugin interface
- Enables building the brownfield as an XCFramework or an AAR which simplifies later usage in the native projects
- Customizability

**Note:** Our goal is maximum customizability, so if you feel like anything else needs to be customizable, please feel free to cut an issue

<a name="compat"></a>
## Platform & Expo SDK compatibility

The plugin supports both Android and iOS. As of now we only support Expo SDK 54

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

<a name="generating-brownfield-targets"></a>
### Adding brownfield targets

The additional targets for brownfield will be added automatically every time you prebuild the native projects

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

More details and full reference of the CLI commands can be found below in the [CLI Reference](#cli) section

<a name="with-manually"></a>
### Building manually

Brownfields can be also built manually using the `xcodebuild` and `./gradlew` commands. Please see [build-xcframework.sh](#./example/scripts/build-xcframework.sh) and [build-aar.sh](#./example/scripts/build-aar.sh) for an example reference of manual building

<a name="cli"></a>
## CLI reference

<a name="cli-commands"></a>
### CLI commands

#### `build-android`

TODO

#### `build-ios`

Builds the iOS frameworks, packages them as a single XCFramework places it in a single directory along with `hermes.xcframework` copied from Pods

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

<a name="configuration"></a>
## Configuration reference 

<a name="configuration-android"></a>
### Android

<a name="configuration-ios"></a>
### iOS

| Property | Description | Default value |
| --- | --- | --- |
| `bundleIdentifier` | Bundle identifier for the brownfield native target. | `ios.bundleIdentifier` with last component replaced with the target name or `com.example.<target-name>` if `ios.bundleIdentifier` is undefined. |
| `targetName` | Name of the brownfield native target. Also used as the name of the directory containing brownfield files. The value is sanitized to only contain alphanumeric characters and start with a letter. | `config.scheme` or `config.ios.scheme` appended with `brownfield`, if either value is defined and a single string. If not defaults to to `<slug>brownfield`, where `<slug>` is sanitized slug from the Expo project config |
