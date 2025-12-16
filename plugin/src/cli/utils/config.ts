import type { Result, Spec } from 'arg';
import type {
  BuildConfigAndroid,
  BuildConfigCommon,
  BuildTypeAndroid,
  BuildTypeCommon,
} from './types';
import { Defaults } from '../constants';
import { inferAndroidLibrary } from './infer';

export const getCommonConfig = (args: Result<Spec>): BuildConfigCommon => {
  return {
    help: !!args['--help'],
    verbose: !!args['--verbose'],
  };
};

export const getAndroidConfig = async (
  args: Result<Spec>,
): Promise<BuildConfigAndroid> => {
  return {
    ...getCommonConfig(args),
    buildType: getBuildTypeAndroid(args),
    libraryName: args['--library'] || (await inferAndroidLibrary()),
    repositories: args['--repository'] || [],
    tasks: args['--task'] || [],
  };
};

export const getIosConfig = (args: Result<Spec>) => {
  return {
    ...getCommonConfig(args),
    artifacts: args['--artifacts'] || Defaults.artifactsPath,
    buildType: getBuildTypeCommon(args),
  };
};

export const getTasksAndroidConfig = async (args: Result<Spec>) => {
  return {
    ...getCommonConfig(args),
    libraryName: args['--library'] || (await inferAndroidLibrary()),
  };
};

export const getBuildTypeCommon = (args: Result<Spec>): BuildTypeCommon => {
  return !args['--release'] && args['--debug'] ? 'debug' : 'release';
};

export const getBuildTypeAndroid = (args: Result<Spec>): BuildTypeAndroid => {
  if (
    (args['--debug'] && args['--release']) ||
    (!args['--debug'] && !args['--release'])
  ) {
    return 'all';
  }

  return getBuildTypeCommon(args);
};
