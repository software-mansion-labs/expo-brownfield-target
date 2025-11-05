<img width="5334" height="2667" alt="expo-brownfield-target-banner" src="https://github.com/user-attachments/assets/7994328f-6401-474a-965d-35829ba4ac41" />

# expo-brownfield-target

## Installation

### Step 1: Installation

Run the following command to add the **expo-brownfield-target** plugin to your project:

```sh
npm install expo-brownfield-target
```

### Step 2: Config plugin setup

Add the config plugin to the `"plugins"` section in your `app.json` or `app.config.js`:

```json
{
  "expo": {
    "name": "my-awesome-expo-project",
    "slug": "my-awesome-expo-project",
    ...
    "plugins": [
      ... // Other plugins
      "expo-brownfield-target"
    ]
  }
}
```

<!-- Optionally you can configure the plugin. For configuration please refer to  -->

Then prebuild your app with:

```sh
npx expo prebuild --clean
```

## API

### iOS

| Property | Description | Default value |
| --- | --- | --- |
| `bundleIdentifier` | Bundle identifier for the brownfield native target. | `ios.bundleIdentifier` with last component replaced with the target name or `com.example.<target-name>` if `ios.bundleIdentifier` is undefined. |
| `targetName` | Name of the brownfield native target. Also used as the name of the directory containing brownfield files. The value is sanitized to only contain alphanumeric characters and start with a letter. | `config.scheme` or `config.ios.scheme` appended with `brownfield`, if either value is defined and a single string. If not defaults to to `<slug>brownfield`, where `<slug>` is sanitized slug from the Expo project config |
