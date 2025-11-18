# Manual Setup

All steps performed by the plugin can be also perfomed manually (e.g. in project that don't use Continuous Native Generation). The below sections cover manual set up of brownfield for iOS and Android (either using **Android Studio** or manually)

### Table of contents
- [Android (Android Studio)](#android-android-studio)
  - [Open the project](#android-as-open)
  - [Library setup](#android-as-lib)
  - [Project structure](#android-as-structure)
  - [Files](#android-as-files)
  - [react-native-brownfield plugin](#android-as-plugin)
  - [Building AAR](#android-as-building)
- [Android (Manually)](#android-manually)
  - [Project structure](#android-m-structure)
  - [Files](#android-m-files)
  - [react-native-brownfield-plugin](#android-m-plugin)
  - [Building AAR](#android-m-building)
- [iOS](#ios)

<!-- SECTION: ANDROID STUDIO -->
<a name="#android-android-studio"></a>
## Android (Android Studio)

<a name="#android-as-open"></a>
### Open the project

Open the `android/` directory of your Expo project with **Android Studio**

<a name="#android-as-lib"></a>
### Library setup

Select `File` > `New` > `New Module...` in the menu. Select `Android Library` template and configure the properties (**Module name**, **Package name**, etc.) if the default values do not meet your project's requirements

<a name="#android-as-structure"></a>
### Project structure

**Android Studio** should automatically set up the directory structure and create some of the files:

```cpp
android/
|_ .gradle/
|_ app/  # app project
|_ brownfield/  # brownfield project
  |_ src/
    |_ androidTest/
    |_ main/
      |_ java/com/swmansion/brownfield/  # sources of the library
      |_ AndroidManifest.xml # empty manifest
    |_ test/
  |_ build.gradle.kts
  |_ consumer-rules.pro  # empty file
  |_ proguard-rules.pro  # default Proguard rules
```

It should also automatically include the library in the root `settings.gradle` file:

```gradle
include ':app'
includeBuild(expoAutolinking.reactNativeGradlePlugin)
include ':brownfield'
```

<a name="#android-as-files"></a>
### Files

Content of the files can be copied from the default templates used by the plugin: [templates/android](./plugin/templates/android/). Please keep in mind that some of those templates contain interplation placeholders (in format of `${{variableName}}`) which should be replaced with values suitable for your project

```kt
package ${{packageId}}
```

You can find full reference on values used by each template and the variable interpolation in [TEMPLATES.md](./TEMPLATES.md)

Some files should be automatically created by **Android Studio** and the default contents should be enough for the library. If any of the file is missing or the contents are different then described below, please align or create the file based on the templates

- `AndroidManifest.xml` - by default should contain an empty manifest
- `proguard-rules.pro` - by default should be empty (all rules should be commented out)
- `consumer-rules.pro` - by default should be empty

Add the following files to the sources directory of the library (`java/com/swmansion/brownfield` in the example above):

- `ReactNativeHostManager.kt`
- `ReactNativeViewFactory.kt`
- `ReactNativeFragment.kt`

Update the contents of the `build.gradle.kts` file of the library based on the template or with custom configuration matching your requirements

<a name="#android-as-plugin"></a>
### react-native-brownfield plugin

Add `brownfield-gradle-plugin` plugin to the root `build.gradle` of the Android project:

```gradle
buildscript {
  ...
  dependencies {
    ...
    classpath("com.callstack.react:brownfield-gradle-plugin:0.5.0")
```

Make sure that the plugin is referenced in the `build.gradle.kts` of the library:

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.facebook.react")
    id("com.callstack.react.brownfield")
    `maven-publish`
}
```

<a name="#android-as-building"></a>
### Building AAR

The library can be built manually:

```sh
./gradlew :brownfield:clean
./gradlew :brownfield:assembleRelease
# If you want to publish to local Maven repo
./gradlew :brownfield:publishMavenAarPublicationToMavenLocal
```

Or with `expo-brownfield-target` CLI. Please see [README.md](./README.md) for full CLI reference:

```sh
npx expo-brownfield-target build-android
```

When the build succeeds a **fat-AAR** (i.e. AAR which includes all) with and (optionally) published to the local Maven repo

<!-- END SECTION: ANDROID STUDIO -->

<!-- SECTION: ANDROID MANUALLY -->
<a name="#android-manually"></a>
## Android (Manually)

<a name="#android-m-structure"></a>
### Project structure

Create the root directory for the brownfield library in the `android/` directory of your Expo project:

```cpp
android/
|_ .gradle/
|_ app/  # app project
|_ brownfield/  # brownfield project
|_ ...
```

Set up the directory structure for the library:

```cpp
android/
|_ .gradle/
|_ app/  # app project
|_ brownfield/  # brownfield project
  |_ src/main/  # main directory of the library
    |_ java/com/swmansion/brownfield/  # sources of the library
|_ ...
```

<a name="#android-m-files"></a>
### Files

Content of the files can be copied from the default templates used by the plugin: [templates/android](./plugin/templates/android/). Please keep in mind that some of those templates contain interplation placeholders (in format of `${{variableName}}`) which should be replaced with values suitable for your project

```kt
package ${{packageId}}
```

You can find full reference on values used by each template and the variable interpolation in [TEMPLATES.md](./TEMPLATES.md)

Add the following files to the root directory of the library:

- `build.gradle.kts`
- `consumer-rules.pro`
- `proguard-rules.pro`

Add `AndroidManifest.xml` at the main directory (`src/main/` in the example above)

Add the following files to the sources directory of the library (`java/com/swmansion/brownfield/` in the example above):

- `ReactNativeHostManager.kt`
- `ReactNativeViewFactory.kt`
- `ReactNativeFragment.kt`

After adding the files your library structure should look like below:

```
android/
|_ .gradle/
|_ app/
|_ brownfield/
  |_ src/
    |_ main/
      |_ java/com/swmansion/brownfield/
        |_ ReactNativeFragment.kt
        |_ ReactNativeHostManager.kt
        |_ ReactNativeViewFactory.kt
      |_ AndroidManifest.xml
  |_ build.gradle.kts
  |_ consumer-rules.pro
  |_ proguard-rules.pro
|_ ...
```

Make sure to include the library project in the `settings.gradle` at the root of the Android project:

```gradle
include ':app'
includeBuild(expoAutolinking.reactNativeGradlePlugin)
include ':brownfield'
```

<a name="#android-m-plugin"></a>
### react-native-brownfield plugin

Add `brownfield-gradle-plugin` plugin to the root `build.gradle` of the Android project:

```gradle
buildscript {
  ...
  dependencies {
    ...
    classpath("com.callstack.react:brownfield-gradle-plugin:0.5.0")
```

Make sure that the plugin is referenced in the `build.gradle.kts` of the library:

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.facebook.react")
    id("com.callstack.react.brownfield")
    `maven-publish`
}
```

<a name="#android-m-building"></a>
### Building AAR

The library can be built manually:

```sh
./gradlew :brownfield:clean
./gradlew :brownfield:assembleRelease
# If you want to publish to local Maven repo
./gradlew :brownfield:publishMavenAarPublicationToMavenLocal
```

Or with `expo-brownfield-target` CLI. Please see [README.md](./README.md) for full CLI reference:

```sh
npx expo-brownfield-target build-android
```

When the build succeeds a **fat-AAR** (i.e. AAR which includes all) with and (optionally) published to the local Maven repo

<!-- END SECTION: ANDROID MANUALLY -->

<!-- SECTION: IOS -->

<a name="ios"></a>
## iOS

<!-- END SECTION: IOS -->
