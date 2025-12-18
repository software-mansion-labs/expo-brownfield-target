# CLI

### Table of Contents

- [Usage](#usage)

- [Commands](#commands)

- [Examples](#examples)

- [Troubleshooting](#troubleshooting)

<a href="usage"></a>
## Usage

```
npx expo-brownfield-target [command] [options]
```

Get help:

```
npx expo-brownfield-target --help
npx expo-brownfield-target [command] --help
```

<a href="commands"></a>
## Commands

`build-android`

Builds and publishes brownfield and its dependencies

```
npx expo-brownfield-target build-android [options]
```

Options:

- `-h, --help` - Display help message

- `--verbose` - Include all logs from subprocesses (e.g. `./gradlew`)

- `-d, --debug` - Build the brownfield in debug mode

- `-r, --release` - Build the brownfield in release mode

- `-a, --all` - Build brownfield in both debug and release mode. Equivalent to specifying both `--debug` and `--release` at once. Default behavior

- `-l, --library` - Specify brownfield library name (default: inferred from the project)

- `--repo, --repository` - Specify one or more Maven repositories to publish artifacts to by their names. Can be passed multiple times to specify multiple repositories

- `-t, --task` - Specify one or more Gradle publish tasks to be run. Can be passed multiple times to specify multiple tasks 

----

<br />

`build-ios`

Builds the brownfield XCFramework and copies the hermes XCFramework to the artifacts directory.

```
npx expo-brownfield-target build-ios [options]
```

Options:

- `-h, --help` - Display help message

- `--verbose` - Include all logs from subprocesses (e.g. `xcodebuild`)

- `-d, --debug` - Build the brownfield in debug mode

- `-r, --release` - Build the brownfield in release mode (takes precedence over `--debug` if both are provided)

- `-a, --artifacts` - Path to the artifacts directory. Relative to the root of the Expo project (default: `./artifacts`)

- `-s, --scheme` - Xcode scheme to be built (default: inferred from the project)

- `-x, --xcworkspace` - Xcode workspace path (default: inferred from the project)

----

<br />

`tasks-android`

Lists all available publish tasks and Maven repositories.

```
npx expo-brownfield-target tasks-android [options]
```

Options:

- `-h, --help` - Display help message

- `--verbose` - Include all logs from subprocesses (e.g. `./gradlew`)

- `-l, --library` - Specify brownfield library name (default: inferred from the project)

<a href="examples"></a>
## Examples

### Building for Android:

List available publish tasks and repositories:

```
npx expo-brownfield-target tasks-android
```

```
✔ Successfully read publish tasks from the android project

Publish tasks:
- publishBrownfieldAllPublicationToCustomLocalRepository
- publishBrownfieldAllPublicationToMavenLocal
- publishBrownfieldAllPublicationToRemotePrivate1Repository
- publishBrownfieldAllPublicationToRemotePublicRepository
- publishBrownfieldDebugPublicationToCustomLocalRepository
- publishBrownfieldDebugPublicationToMavenLocal
- publishBrownfieldDebugPublicationToRemotePrivate1Repository
- publishBrownfieldDebugPublicationToRemotePublicRepository
- publishBrownfieldReleasePublicationToCustomLocalRepository
- publishBrownfieldReleasePublicationToMavenLocal
- publishBrownfieldReleasePublicationToRemotePrivate1Repository
- publishBrownfieldReleasePublicationToRemotePublicRepository

Repositories:
- CustomLocal
- MavenLocal
- RemotePrivate1
- RemotePublic
```

Build and publish to one or more repositories:

```
npx expo-brownfield-target build-android \
  -t publishBrownfieldAllPublicationToCustomLocalRepository \
  -t publishBrownfieldAllPublicationToMavenLocal
```

```
Build configuration:
- Verbose: false
- Build type: All
- Brownfield library: brownfield
- Repositories: []
- Tasks: publishBrownfieldAllPublicationToCustomLocalRepository, publishBrownfieldAllPublicationToMavenLocal

✔ Running task: publishBrownfieldAllPublicationToCustomLocalRepository succeeded
✔ Running task: publishBrownfieldAllPublicationToMavenLocal succeeded
```

### Building for iOS

Build the brownfield XCFramework and copy hermes XCFramework:

```
npx expo-brownfield-target
```

```
Build configuration:
- Verbose: false
- Artifacts directory: /Users/swm/Desktop/my-expo-app/artifacts
- Build type: Release
- Xcode Scheme: MyBrownfieldApp
- Xcode Workspace: myexpoapp.xcworkspace
```

Which should output the XCFrameworks at the artifacts directory:

```
ls ./artifacts
```

```
MyBrownfieldApp.xcframework
hermes.xcframework
```

<a href="troubleshooting"></a>
## Troubleshooting

### Build configuration inference fails (e.g. for Android library name or Xcode workspace)

**Solution**: Verify if you're running CLI commands from the root of the Expo project and if you have run `npx expo prebuild` before using them

### Build fails with an unclear error

**Solution:** Ensure you are running the command with `--verbose` option. If the issue still persists please cut an issue on GitHub

```
npx expo-brownfield-target build-ios --verbose
```

### Only part of specified repositories and tasks is build

**Solution:** The CLI allows specifying the configuration both with task names and repository names, but if both variants are used, tasks take precedence over repositories 

Ensure you are specifying the configuration either using only repositories:

```
npx expo-brownfield-target --repo repo1 --repository repo2
```

Or only the task names:

```
npx expo-brownfield-target \
  -t publishBrownfieldAllPublicationToMavenLocal \
  --task publishBrownfieldAllPublicationToRemotePublicRepository
```
