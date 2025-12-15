import { helpMessage } from '../utils';

/**
 * General help message
 */
const generalHelp = helpMessage({
  commands: [
    {
      command: 'build-android',
      description: 'build and publish Android brownfield artifacts',
      hasOptions: true,
    },
    {
      command: 'build-ios',
      description: 'build iOS brownfield artifacts',
      hasOptions: true,
    },
  ],
  options: [
    {
      description: 'output the version number',
      option: '--version',
      short: '-v',
    },
    {
      description: 'display help for command',
      option: '--help',
      short: '-h',
    },
  ],
});

/**
 * Help message for 'build-android' command
 */
const buildAndroidHelp = helpMessage({
  promptCommand: 'build-android',
  options: [
    {
      description: "display help for 'build-android'",
      option: '--help',
      short: '-h',
    },
    {
      description: 'do not publish AAR to Maven',
      option: '--no-publish',
    },
    {
      description: 'run custom tasks after building AAR',
      option: '--tasks',
      short: '-t',
    },
    {
      description: 'build AAR in debug configuration',
      option: '--debug',
      short: '-d',
    },
    {
      description: 'build AAR in release configuration',
      option: '--release',
      short: '-r',
    },
    {
      description: 'output all subcommands output to the terminal',
      option: '--verbose',
    },
    {
      description: 'path to artifacts directory',
      option: '--artifacts',
      short: '-a',
    },
    {
      description: 'name of the brownfield library',
      option: '--library',
      short: '-l',
    },
  ],
});

/**
 * Help message for 'build-ios' command
 */
const buildIosHelp = helpMessage({
  promptCommand: 'build-ios',
  options: [
    {
      description: "display help for 'build-ios'",
      option: '--help',
      short: '-h',
    },
    {
      description: 'scheme to be build',
      option: '--scheme',
      short: '-s',
    },
    {
      description: 'path to .xcworkspace',
      option: '--xcworkspace',
      short: '-x',
    },
    {
      description: 'build xcframework in debug configuration',
      option: '--debug',
      short: '-d',
    },
    {
      description: 'build xcframework in release configuration',
      option: '--release',
      short: '-r',
    },
    {
      description: 'output all subcommands output to the terminal',
      option: '--verbose',
    },
    {
      description: 'path to artifacts directory',
      option: '--artifacts',
      short: '-a',
    },
  ],
});

/**
 * Help messages
 */
export const Help = {
  Android: buildAndroidHelp,
  General: generalHelp,
  IOS: buildIosHelp,
} as const;
