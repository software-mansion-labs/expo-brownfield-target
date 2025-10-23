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
