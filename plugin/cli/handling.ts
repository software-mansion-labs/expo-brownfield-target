import {
  BUILD_HELP_MESSAGE,
  MISSING_PLATFORM_VALUE,
  UNKNOWN_OPTION_MESSAGE,
  UNKNOWN_PLATFORM_MESSAGE,
} from './messages';
import type { BuildConfig } from './types';

const isSupportedPlatform = (platform: string): platform is 'android' | 'ios' =>
  platform === 'android' || platform === 'ios';

const parseOptions = (options: string[]): BuildConfig => {
  if (options.includes('-h') || options.includes('--help')) {
    return { help: true };
  }

  const config: BuildConfig = {};
  let index = 0;
  while (index < options.length) {
    if (options[index] === '--platform' || options[index] === '-p') {
      if (index + 1 >= options.length) {
        console.error(MISSING_PLATFORM_VALUE);
        process.exit(1);
      }

      const platform = options[index + 1];
      if (!isSupportedPlatform(platform)) {
        console.error(UNKNOWN_PLATFORM_MESSAGE(platform));
        process.exit(1);
      }

      config[platform] = true;
      index += 2;
    } else if (
      options[index] === '-pandroid' ||
      options[index] === '--platform=android'
    ) {
      config['android'] = true;
      index += 1;
    } else if (
      options[index] === '-pios' ||
      options[index] === '--platform=ios'
    ) {
      config['ios'] = true;
      index += 1;
    } else {
      console.log(UNKNOWN_OPTION_MESSAGE(options[index]));
      process.exit(1);
    }
  }

  return config;
};

export const handleBuild = (options: string[]) => {
  const config = parseOptions(options);
  const fallbackPlatforms = !config.android && !config.ios;

  if (config.help) {
    console.log(BUILD_HELP_MESSAGE);
    process.exit(0);
  }

  if (config.android || fallbackPlatforms) {
    console.log('Android');
  }

  if (config.ios || fallbackPlatforms) {
    console.log('iOS');
  }
};
