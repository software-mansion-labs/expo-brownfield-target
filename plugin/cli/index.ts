#!/usr/bin/env node
// @ts-expect-error - the directory structure is different after building
import { version } from '../../../package.json';
import { buildAndroid } from './build-android';
import { buildIOS } from './build-ios';
import { GENERAL_HELP_MESSAGE, UNKNOWN_COMMAND_MESSAGE } from './messages';
import type { CLIAction } from './types';

const isSupportedCommand = (command: string): command is CLIAction => {
  return ['build-ios', 'build-android'].includes(command);
};

const parseArgs = (args: string[]): CLIAction => {
  if (args.length < 1 || args[0] === '-h' || args[0] === '--help') {
    return 'help';
  }

  if (args[0] === '-v' || args[0] === '--version') {
    return 'version';
  }

  if (isSupportedCommand(args[0])) {
    return args[0];
  }

  return 'unknown';
};

const main = async () => {
  const args = process.argv.slice(2);
  const action = parseArgs(args);

  /* eslint-disable no-fallthrough */
  switch (action) {
    case 'build-android':
      await buildAndroid(args.slice(1));
      process.exit(0);
    case 'build-ios':
      await buildIOS(args.slice(1));
      process.exit(0);
    case 'help':
      console.log(GENERAL_HELP_MESSAGE);
      process.exit(0);
    case 'version':
      console.log(version);
      process.exit(0);
    case 'unknown':
      console.log(UNKNOWN_COMMAND_MESSAGE(args[0]));
      process.exit(1);
    default:
      process.exit(0);
  }
  /* eslint-enable no-fallthrough */
};

main();
