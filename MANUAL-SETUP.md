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
  - [Open the project](#ios-open)
  - [Framework setup](#ios-framework)
  - [Files](#ios-files)
  - [Build configuration](#ios-config)
  - [Building XCFramework](#ios-xcf)

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

**Note:** If your library name is different than `brownfield` please make sure to use `build-android` with `-l` / `--library` flag:

```sh
npx expo-brownfield-target build-android -l mybrownfield
```

When the build succeeds a **fat-AAR** (i.e. AAR which also includes all needed dependencies) with brownfield and (optionally) published to the local Maven repo

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

**Note:** If your library name is different than `brownfield` please make sure to use `build-android` with `-l` / `--library` flag:

```sh
npx expo-brownfield-target build-android -l mybrownfield
```

When the build succeeds a **fat-AAR** (i.e. AAR which includes all) with and (optionally) published to the local Maven repo

<!-- END SECTION: ANDROID MANUALLY -->

<!-- SECTION: IOS -->

<a name="ios"></a>
## iOS

<a name="#ios-open"></a>
### Open the project

Open the `ios/` directory of your Expo project with **Xcode**

<a name="#ios-framework"></a>
### Framework setup

Select `File` > `+ New` > `Target...` in the menu to create a new target in the project. Choose the `Framework & Library` > `Framework` template from the `iOS` tab. Configure the properties (Product Name, identifiers, etc.) to meet your project's requirements

After confirming with `Finish` a new directory named the same as the new target should become visible in the Project Navigator. Right-click on it to open the menu and select `Convert to Group` as CocoaPods has some issues when working with the references

<a name="#ios-files"></a>
### Files

Content of the files can be copied from the default templates used by the plugin: [templates/ios](./plugin/templates/ios/). Please keep in mind that some of those templates contain interplation placeholders (in format of `${{variableName}}`) which should be replaced with values suitable for your project

```xml
<key>CFBundleName</key>
<string>${{targetName}}</string>
<key>CFBundlePackageType</key>
```

You can find full reference on values used by each template and the variable interpolation in [TEMPLATES.md](./TEMPLATES.md)

Add the following files to the framework directory:

- `ExpoApp.swift`
- `Info.plist`
- `ReactNativeView.swift` (if you want to include SwiftUI support)

Copy the contents of `Template.entitlements` file to a new file named `<target-name>` (e.g. `MyBrownfield.entitlements`) at the framework directory

<a name="#ios-config"></a>
### Build configuration

In the project view select the `Build Settings` tab and make sure you're editing the framework target. Select the `All` tab to be able to edit all values. Ensure that the following settings have the right values:

| Setting | Value |
|---|---|
| Build Libraries for Distribution | Yes |
| User Script Sandboxing | No |
| Skip Install | No |
| Enable Module Verifier | No |

Then navigate to the `Build Phases` of the app target and copy the contents of the `Bundle React Native code and images` step. Create a new `Run Script Phase` in the brownfield target and paste the copied contents to it. Place it after the `Copy Bundle Resources` step

Make sure that to also copy the `Input Files` values of the phase

Add the framework target to the app target in the `Podfile`:

```ruby
target '<app-target>' do
...
 target '<framework-target>' do
   inherit! :complete
 end
end
```

Add another `Run Script Phase` to the brownfield target:

```sh
FILE="${SRCROOT}/Pods/Target Support Files/Pods-${{projectName}}-${{targetName}}/ExpoModulesProvider.swift"
TEMP_FILE="$FILE.temp"

if [ -f "$FILE" ]; then
  echo "Patching $FILE to hide Expo from public interface"
  sed \\
    -e 's/^import EX/internal import EX/' \\
    -e 's/^import Ex/internal import Ex/' \\
    -e 's/public class ExpoModulesProvider/internal class ExpoModulesProvider/' "$FILE" > "$TEMP_FILE"
  mv "$TEMP_FILE" "$FILE"
fi
```

Make sure to replace the variable placeholders (`${{projectName}}`, `${{targetName}}`) with the values  from your project. Place the value after a step named `[Expo] Configure project`

<a name="#ios-xcf"></a>
### Building XCFramework 

Reinstall the pods with static linking enabled:

```sh 
rm -rf Pods Podfile.lock
USE_FRAMEWORKS=static pod install
```

The XCFramework can be built manually - please use [build-xcframework.sh](./example/scripts/build-xcframework.sh) as the reference or with the CLI:

```
npx expo-brownfield-target build-ios
```

Please see [README.md](./README.md) for full CLI reference

When the build finishes an XCFramework named `<target-name>.xcframework` should be created. Make sure to copy the `hermes.xcframework` file from Pods (`Pods/hermes-engine/destroot/Library/Frameworks/universal/hermes.xcframework`) and ship it/include it in the Swift Package along with the brownfield XCFramework

<!-- END SECTION: IOS -->
