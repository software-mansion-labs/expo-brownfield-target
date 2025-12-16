import arg, { type Result, type Spec } from 'arg';
import {
  getAndroidConfig,
  getBuildTypeAndroid,
  getBuildTypeCommon,
  getCommonConfig,
  getTasksAndroidConfig,
} from '../../../plugin/src/cli/utils/config';
import { inferAndroidLibrary } from '../../../plugin/src/cli/utils/infer';

const parseArgsHelper = (
  args: Spec,
  argv: string[],
  stopAtPositional?: boolean,
): Result<Spec> => {
  return arg(args, { argv, stopAtPositional });
};

const SPEC: Spec = {
  '--all': arg.COUNT,
  '--debug': arg.COUNT,
  '--release': arg.COUNT,
  '--help': arg.COUNT,
  '--verbose': arg.COUNT,
  '--task': [String],
  '--repository': [String],
  '--library': String,
};

jest.mock('../../../plugin/src/cli/utils/infer', () => ({
  inferAndroidLibrary: jest.fn(),
}));

/**
 * getCommonConfig
 */
describe('getCommonConfig', () => {
  it('should return { help: false, verbose: false } if no arguments are passed', () => {
    const parsed = parseArgsHelper(SPEC, []);
    const result = getCommonConfig(parsed);
    expect(result.help).toBe(false);
    expect(result.verbose).toBe(false);
  });

  it('should return { help: true, verbose: false } if --help is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--help']);
    const result = getCommonConfig(parsed);
    expect(result.help).toBe(true);
    expect(result.verbose).toBe(false);
  });

  it('should return { help: false, verbose: true } if --verbose is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--verbose']);
    const result = getCommonConfig(parsed);
    expect(result.help).toBe(false);
    expect(result.verbose).toBe(true);
  });

  it('should return { help: true, verbose: true } if --help and --verbose are passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--help', '--verbose']);
    const result = getCommonConfig(parsed);
    expect(result.help).toBe(true);
    expect(result.verbose).toBe(true);
  });
});

/**
 * getAndroidConfig
 */
describe('getAndroidConfig', () => {
  it('should return empty array if no tasks are passed', async () => {
    const parsed = parseArgsHelper(SPEC, []);
    const result = await getAndroidConfig(parsed);
    expect(result.tasks).toEqual([]);
  });

  it('should return array of tasks if tasks are passed', async () => {
    const parsed = parseArgsHelper(SPEC, [
      '--task',
      'publishBrownfieldReleasePublicationToMavenLocal',
      '--task',
      'publishBrownfieldDebugPublicationToMavenLocal',
    ]);
    const result = await getAndroidConfig(parsed);
    expect(result.tasks).toEqual([
      'publishBrownfieldReleasePublicationToMavenLocal',
      'publishBrownfieldDebugPublicationToMavenLocal',
    ]);
  });

  it('should return empty array if no repositories are passed', async () => {
    const parsed = parseArgsHelper(SPEC, []);
    const result = await getAndroidConfig(parsed);
    expect(result.repositories).toEqual([]);
  });

  it('should return array of repositories if repositories are passed', async () => {
    const parsed = parseArgsHelper(SPEC, [
      '--repository',
      'MavenLocal',
      '--repository',
      'CustomLocal',
    ]);
    const result = await getAndroidConfig(parsed);
    expect(result.repositories).toEqual(['MavenLocal', 'CustomLocal']);
  });

  it('includes common config', async () => {
    const parsed = parseArgsHelper(SPEC, ['--verbose']);
    const result = await getAndroidConfig(parsed);
    expect(result.help).toBe(false);
    expect(result.verbose).toBe(true);
  });
});

// TODO: Add tests for getIosConfig

/**
 * getTasksAndroidConfig
 */
describe('getTasksAndroidConfig', () => {
  it('should infer library name if no library name is passed', async () => {
    (inferAndroidLibrary as jest.Mock).mockResolvedValue('resolvedbrownfield');
    const parsed = parseArgsHelper(SPEC, []);
    const result = await getTasksAndroidConfig(parsed);
    expect(result.libraryName).toEqual('resolvedbrownfield');
  });

  it('should return library name if library name is passed', async () => {
    const parsed = parseArgsHelper(SPEC, ['--library', 'mybrownfield']);
    const result = await getTasksAndroidConfig(parsed);
    expect(result.libraryName).toEqual('mybrownfield');
  });

  it('includes common config', async () => {
    const parsed = parseArgsHelper(SPEC, ['--help']);
    const result = await getTasksAndroidConfig(parsed);
    expect(result.help).toBe(true);
    expect(result.verbose).toBe(false);
  });
});

/**
 * getBuildTypeCommon
 */
describe('getBuildTypeCommon', () => {
  it('should return "debug" if only --debug is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--debug']);
    const result = getBuildTypeCommon(parsed);
    expect(result).toBe('debug');
  });

  it('should return "release" if only --release is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--release']);
    const result = getBuildTypeCommon(parsed);
    expect(result).toBe('release');
  });

  it('should return "release" if both --debug and --release are passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--debug', '--release']);
    const result = getBuildTypeCommon(parsed);
    expect(result).toBe('release');
  });

  it('should return "release" if no build type arguments are passed', () => {
    const parsed = parseArgsHelper(SPEC, []);
    const result = getBuildTypeCommon(parsed);
    expect(result).toBe('release');
  });
});

describe('getBuildTypeAndroid', () => {
  it('should return "all" if only --all is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--all']);
    const result = getBuildTypeAndroid(parsed);
    expect(result).toBe('all');
  });

  it('should return "release" if only --release is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--release']);
    const result = getBuildTypeAndroid(parsed);
    expect(result).toBe('release');
  });

  it('should return "debug" if only --debug is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--debug']);
    const result = getBuildTypeAndroid(parsed);
    expect(result).toBe('debug');
  });

  it('should return "all" if both --debug and --release is passed', () => {
    const parsed = parseArgsHelper(SPEC, ['--debug', '--release']);
    const result = getBuildTypeAndroid(parsed);
    expect(result).toBe('all');
  });

  it('should return "all" if no build type arguments are passed', () => {
    const parsed = parseArgsHelper(SPEC, []);
    const result = getBuildTypeAndroid(parsed);
    expect(result).toBe('all');
  });
});
