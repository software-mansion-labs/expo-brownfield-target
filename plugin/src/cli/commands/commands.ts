import { runBuildAndroid, runTasksAndroid } from './android';
import { runHelp, runVersion } from './general';
import { CommandsMap } from './types';

export const Commands: CommandsMap = {
  'build-android': {
    run: runBuildAndroid,
  },
  'build-ios': {
    run: async () => {},
  },
  'help': {
    run: runHelp,
  },
  'tasks-android': {
    run: runTasksAndroid,
  },
  'version': {
    run: runVersion,
  },
};
