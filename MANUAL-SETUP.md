# Manual Setup

All steps performed by the plugin can be also perfomed manually (e.g. in project that don't use Continuous Native Generation). The below sections cover manual set up of brownfield for iOS and Android (either using **Android Studio** or manually)

### Table of contents
- [Android](#android)
  - [Library setup](#android-library)
    - [Manual setup](#android-lib-ms)
    - [Android Studio](#android-lib-as)
  - [Files](#android-files)
  - [Plugins](#android-plugins)
  - [Building](#android-building)
- [iOS](#ios)
  - [Open the project](#ios-open)
  - [Framework setup](#ios-framework)
  - [Files](#ios-files)
  - [Build configuration](#ios-config)
  - [Building XCFramework](#ios-xcf)

<!-- SECTION: ANDROID -->

<a name="android"></a>
## Android

<a name="android-library"></a>
### Library setup

<a name="android-lib-ms"></a>
#### Manual setup

Create the directory for the brownfield library in the `android/` directory of your Expo project:

```bash
android/
|_ .gradle/
|_ app/  # app project
|_ brownfield/  # brownfield project
|_ ...
```

Be sure to include the newly added library in the root `settings.gradle` file:

```groovy
include ':app'
includeBuild(expoAutolinking.reactNativeGradlePlugin)
include ':brownfield'
```

And to set up the correct directory structure for the library:

```bash
android/
|_ .gradle/
|_ app/  # app project
|_ brownfield/  # brownfield project
  |_ src/main/  # main directory of the library
    |_ java/com/swmansion/brownfield/  # sources of the library
|_ ...
```

<br />

---

<a name="android-lib-as"></a>
#### Android Studio:

Select `File > New > New Module...` in the menu. Select `Android Library` template and configure the properties (Module name, Package name, etc.) if the default values do not meet your project's requirements.

Android Studio should automatically add your project to the root `settings.gradle` file:

```groovy
include ':app'
includeBuild(expoAutolinking.reactNativeGradlePlugin)
include ':brownfield'
```

And create some of the files and directories:

```bash
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

<a name="android-files"></a>
### Files

Content of the files can be copied from the default templates used by the plugin: [templates/android](./plugin/src/templates/android). Please keep in mind that some of those templates contain interpolation placeholders (in format of `${{variableName}}`) which should be replaced with values suitable for your project.

You can find full reference on values used by each template and the variable interpolation in [TEMPLATES.md](./TEMPLATES.md).

Some of the below files might be automatically added if you used Android Studio to initialize the library. In that case it's worth to check if the contents are aligned with the template or with your intended content.

Make sure that the root directory of the library includes the following files:

- `build.gradle.kts`
- `consumer-rules.pro`
- `proguard-rules.pro`

The main directory (`src/main/`) should include a manifest file `AndroidManifest.xml`.

And the sources directory (e.g. `src/main/java/com/swmansion/brownfield`) should include the three files:

- `ReactNativeHostManager.kt`
- `ReactNativeViewFactory.kt`
- `ReactNativeFragment.kt`

After adding the files your library structure should look like below:

```bash
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

<a name="android-plugins"></a>
### Plugins

To properly set up the brownfield library and publish all needed artifacts to Maven repositories you need to include the Gradle plugins which are shipped as part of `expo-brownfield-target` npm package.

Add the following fragment to the root `settings.gradle` file:

```groovy
def brownfieldPluginsPath = new File(
  providers.exec {
    workingDir(rootDir)
    commandLine("node", "--print", "require.resolve('expo-brownfield-target/package.json')")
  }.standardOutput.asText.get().trim(),
  "../gradle-plugins"
).absolutePath
includeBuild(brownfieldPluginsPath)
```

Add the following lines to the root `build.gradle` file of the project to apply the publishing plugin:

```groovy
buildscript {
  repositories {
    ...
  }
  dependencies {
    ...
+    classpath('expo.modules:publish')
  }
}

...

apply plugin: "expo-root-project"
+   apply plugin: "expo-brownfield-publish"
apply plugin: "com.facebook.react.rootproject"
```

And add its configuration to the same file:

```groovy
expoBrownfieldPublishPlugin {
  libraryName = "brownfield"
  publications {
    localDefault {
        type = "localMaven"
    }
    customLocal {
        type = "localDirectory"
        url = "file:///Users/patrykmleczek/Desktop/expo-brownfield-target/example/app/maven"
    }
    remotePublic {
        type = "remotePublic"
        url = "http://localhost:8081/repository/remote-public"
        allowInsecure = true
    }
    remotePrivate {
        type = "remotePrivate"
        url = "http://localhost:8081/repository/remote-basic-auth"
        username = "user"
        password = "user1234"
        allowInsecure = true
    }
  }
}
```

Where `libraryName` is the name of your brownfield library project and `publications` block specifies one or more repositories that you want to publish the artifacts to. Name of each block should be unique across the publications.

Make sure that `build.gradle.kts` file of the brownfield library includes `expo-brownfield-setup` plugin:

```kts
plugins {
  ...
  id("expo-brownfield-setup")
}
```

The plugins will be compiled along with the Android project.

<a name="android-building"></a>
### Building

#### Tasks

The tasks that can be used to build and publish artifacts follow the below name convention:

```
publishBrownfield(All|Debug|Release)PublicationTo<Repository name>
```

Where `All`, `Debug` and `Release` specifies if both or only debug or release variants should be published. Repository name is based on the block names passed in `build.gradle` in PascalCase convention (first character of each word capitalized) and suffixed with `Repository`. For example:

TODO: Write about exception for Maven Local?

```bash
# For the default Maven repository
publishBrownfieldAllPublicationToMavenLocal
# For repository named: `customLocal`
publishBrownfieldDebugPublicationToCustomLocalRepository
```

You can view all available tasks by running:

```bash
./gradlew tasks --all
```

Optionally you can filter out only the tasks that follow the above convention (if your library name is different pass it instead of `brownfield` in the below command):

```bash
./gradlew tasks --all | grep -E 'brownfield:publishBrownfield.*PublicationTo'
```

#### Building

You can run the tasks described aboce to invoke the build and publishing process for the brownfield library and all of its dependencies:

```bash
./gradlew publishBrownfieldAllPublicationToCustomLocalRepository
```

You can also invoke them using the CLI:

TODO: Add sub-section about the CLI

<!-- END SECTION: ANDROID -->

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

Content of the files can be copied from the default templates used by the plugin: [templates/ios](./plugin/templates/ios/). Please keep in mind that some of those templates contain interplation placeholders (in format of `${{variableName}}`) which should be replaced with values suitable for your project. For example:

```xml
<key>CFBundleName</key>
<string>${{targetName}}</string>
<key>CFBundlePackageType</key>
```

You can find full reference on values used by each template and the variable interpolation in [TEMPLATES.md](./TEMPLATES.md).

Add the following files to the framework directory:

- `ExpoApp.swift`
- `Info.plist`
- `Messaging.swift` (if you want to include support for the bi-directional messaging API)
- `ReactNativeView.swift` (if you want to include SwiftUI support)
- `ReactNativeViewController.swift` (if you want to include UIKit view controller)

Copy the contents of `Template.entitlements` file to a new file named `<target-name>` (e.g. `MyBrownfield.entitlements`) at the framework directory.

The `patch-expo.sh` template shouldn't be copied anywhere and will come into use later to define a run script phase for patching `ExpoModulesProvider.swift` file.

<a name="#ios-config"></a>
### Build configuration

In the project view select the `Build Settings` tab and make sure you're editing the framework target. Select the `All` tab to be able to edit all values. Ensure that the following settings have the right values:

| Setting | Value |
|---|---|
| Build Libraries for Distribution | Yes |
| User Script Sandboxing | No |
| Skip Install | No |
| Enable Module Verifier | No |

Then navigate to the `Build Phases` of the app target and copy the contents of the `Bundle React Native code and images` step. Create a new `Run Script Phase` in the brownfield target and paste the copied contents to it. Place it after the `Copy Bundle Resources` step.

Make sure that to also copy the `Input Files` values of the phase.

Add the framework target to the app target in the `Podfile`:

```ruby
target '<app-target>' do
...
 target '<framework-target>' do
   inherit! :complete
 end
end
```

Add another `Run Script Phase` to the brownfield target (based on the `patch-expo.sh` template):

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

Make sure to replace the variable placeholders (`${{projectName}}`, `${{targetName}}`) with the values suitable for your project. Place the value after a step named `[Expo] Configure project`

<a name="#ios-xcf"></a>
### Building XCFramework 

Reinstall the pods with static linking enabled:

```sh 
rm -rf Pods Podfile.lock
USE_FRAMEWORKS=static pod install
```

The XCFramework can be built manually - please use [build-xcframework.sh](./example/scripts/build-xcframework.sh) as the reference.

It can also be built using the CLI:

```
npx expo-brownfield-target build-ios
```

Please see [README.md](./README.md) for the full reference for the CLI.

When the build finishes an XCFramework named `<target-name>.xcframework` should be created. Make sure to copy the `hermes.xcframework` file from Pods (`Pods/hermes-engine/destroot/Library/Frameworks/universal/hermes.xcframework`) and ship it/include it in the Swift Package along with the brownfield XCFramework.

For examples of usage in the native apps and reference for the available APIs please see [README.md](./README.md).

<!-- END SECTION: IOS -->
