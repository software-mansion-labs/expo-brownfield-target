export const GENERAL_HELP_MESSAGE = `
Usage: expo-brownfield-target <command> [options]

Options:
  -v, --version     output the version number
  -h, --help        display help for command

Commands:
  build [options]
`;

export const BUILD_HELP_MESSAGE = `
Usage: expo-brownfield-target build [options]

Options:
  -h, --help        display help for command for 'build'
`;

export const UNKNOWN_COMMAND_MESSAGE = (command: string): string =>
  `Unknown command: '${command}'
Supported commands: build
`;

export const UNKNOWN_PLATFORM_MESSAGE = (platform: string): string =>
  `Unknown platform: '${platform}'
Supported platfroms: android, ios
`;

export const UNKNOWN_OPTION_MESSAGE = (option: string): string =>
  `Unknown option for 'build': '${option}'`;

export const MISSING_PLATFORM_VALUE = `Platform option (-p/--platform) requires a value to be passed
Example: ... -p ios --platform=android`;
