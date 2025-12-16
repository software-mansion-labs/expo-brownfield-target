import chalk from 'chalk';
import { BuildConfigAndroid, BuildConfigIos } from './types';

const isBuildConfigAndroid = (
  config: BuildConfigAndroid | BuildConfigIos,
): config is BuildConfigAndroid => {
  return 'libraryName' in config;
};

export const printConfig = (config: BuildConfigAndroid | BuildConfigIos) => {
  console.log(chalk.bold('Build configuration:'));
  console.log(`- Verbose: ${config.verbose}`);

  if (isBuildConfigAndroid(config)) {
    console.log(
      `- Build type: ${config.buildType.charAt(0).toUpperCase() + config.buildType.slice(1)}`,
    );
    console.log(`- Brownfield library: ${config.libraryName}`);
    console.log(
      `- Repositories: ${config.repositories.length > 0 ? config.repositories.join(', ') : '[]'}`,
    );
    console.log(
      `- Tasks: ${config.tasks.length > 0 ? config.tasks.join(', ') : '[]'}`,
    );
  } else {
    console.log(`- Artifacts directory: ${config.artifacts}`);
    console.log(
      `- Build type: ${config.buildType.charAt(0).toUpperCase() + config.buildType.slice(1)}`,
    );
    console.log(`- Xcode Scheme: ${config.scheme}`);
    console.log(`- Xcode Workspace: ${config.workspace}`);
  }

  console.log('');
};
