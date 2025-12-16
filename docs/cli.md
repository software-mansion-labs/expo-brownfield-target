# CLI

### Table of Contents

- Usage

- Commands

- Examples

## Usage

Basic syntax:

```
npx expo-brownfield-target [command] [options]
```

Get help:

```
npx expo-brownfield-target --help
npx expo-brownfield-target [command] --help
```

## Commands

`build-android`

`build-ios`

----

`tasks-android`

Lists all available publish tasks and Maven repositories.

```
npx expo-brownfield-target tasks-android [options]
```

Options:

- `-h, --help` - Display help message

- `-l, --library` - Specify brownfield library name (default: inferred from the project)

## Examples

Building for Android

```
# List available repositories and tasks
npx expo-brownfield-target tasks-android
```
