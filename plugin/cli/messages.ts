export const GENERAL_HELP_MESSAGE = `
Usage: expo-brownfield-target <command> [options]

Options:
  -v, --version     output the version number
  -h, --help        display help for command

Commands:
  build [options]
`;

export const BUILD_ANDROID_HELP_MESSAGE = `
Usage: expo-brownfield-target build-android [options]

Options:
  -h, --help        display help for command for 'build-android'
`;

export const BUILD_IOS_HELP_MESSAGE = `
Usage: expo-brownfield-target build-ios [options]

Options:
  -h, --help        display help for command for 'build-ios'
`;

export const UNKNOWN_COMMAND_MESSAGE = (command: string): string =>
  `Unknown command: '${command}'
Supported commands: build-android, build-ios
`;

export const UNKNOWN_OPTION_MESSAGE = (option: string): string =>
  `Unknown option for 'build': '${option}'`;

export const MISSING_PLATFORM_VALUE = `Platform option (-p/--platform) requires a value to be passed
Example: ... -p ios --platform=android`;
