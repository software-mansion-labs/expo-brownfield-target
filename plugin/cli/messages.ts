import chalk from 'chalk';

export const GENERAL_HELP_MESSAGE = `
${chalk.bold('Usage:')} expo-brownfield-target <command> [options]

${chalk.bold('Options:')}
  -v, --version     output the version number
  -h, --help        display help for command

${chalk.bold('Commands:')}
  build-android [options]     build brownfield for Android
  build-ios [options]         build brownfield for iOS
`;

export const BUILD_ANDROID_HELP_MESSAGE = `
${chalk.bold('Usage:')} expo-brownfield-target build-android [options]

${chalk.bold('Options:')}
  -h, --help           display help for 'build-android'
  --no-publish         do not publish AAR to Maven
  -d, --debug          build AAR in debug configuration
  -r, --release        build AAR in release configuration
`;

export const BUILD_IOS_HELP_MESSAGE = `
${chalk.bold('Usage:')} expo-brownfield-target build-ios [options]

${chalk.bold('Options:')}
  -h, --help           display help for 'build-ios'
  -s, --scheme         scheme to be build
  -x, --xcworkspace    path to .xcworkspace
  -d, --debug          build xcframework in debug configuration
  -r, --release        build xcframework in release configuration
`;

export const UNKNOWN_COMMAND_MESSAGE = (command: string): string =>
  `Unknown command: '${command}'
Supported commands: build-android, build-ios
`;
