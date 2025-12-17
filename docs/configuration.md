# Configuration

### Table of Contents

- [Overview](#overview)

- [Configuration Options](#options)
  - [Android](#options-android)
  - [iOS](#options-ios)

- [File templates](#templates)

<a href="overview"></a>

## Overview

The brownfield setup can be configured by passing the options via the config plugin interface or using the file templates.

Our goal is maximum configurability, so if you feel like anything else needs to be customizable, please feel free to cut an issue or a discussion with a feature request.

<a href="options"></a>

## Configuration

You can configure various properties of the native projects like package identifiers, target names or versions via the config plugin interface in `app.json`/`app.config.js`/`app.config.ts` file.

```json
"plugins": [
  {
    "expo-brownfield-target",
    {
      "android": {
        ...
      },
      "ios": {
        ...
      }
    }
  }
]
```

<a href="options-android"></a>

### Android

| Property     | Description                                                                                   | Default value                                                                                               |
| ------------ | --------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `library`    | Name of the Android library used for the brownfield                                           | `brownfield`                                                                                                |
| `package`    | Package identifier for the brownfield library                                                 | `android.package` appended with `.brownfield` or `com.example.brownfield` if `android.package` is undefined |
| `group`      | Group property for the brownfield library                                                     | Resolved value of the `package` stripped of the last component                                              |
| `version`    | Version of the brownfield library                                                             | 1.0.0                                                                                                       |
| `publishing` | An array of Maven publishing configurations. For more detailed reference see the next section | `[{ type: 'localMaven' }]`                                                                                  |

See [publishing.md](./publishing.md) for full reference of the Android publishing configuration.

<a href="options-ios"></a>

### iOS

| Property           | Description                                                                                                                                                                                       | Default value                                                                                                                                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `bundleIdentifier` | Bundle identifier for the brownfield native target.                                                                                                                                               | `ios.bundleIdentifier` with last component replaced with the target name or `com.example.<target-name>` if `ios.bundleIdentifier` is undefined.                                                                            |
| `targetName`       | Name of the brownfield native target. Also used as the name of the directory containing brownfield files. The value is sanitized to only contain alphanumeric characters and start with a letter. | `config.scheme` or `config.ios.scheme` appended with `brownfield`, if either value is defined and a single string. If not defaults to to `<slug>brownfield`, where `<slug>` is sanitized slug from the Expo project config |

<a href="templates"></a>

## File templates

You can also overwrite the templates which are used to generate the files to even better suit the plugin behavior to your requirements. More information about overwriting the templates can be found in [templates.md](./templates.md).
