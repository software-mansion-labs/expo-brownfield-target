import { Args, Help } from '../../constants';
import {
  BuildTypeAndroid,
  getAndroidConfig,
  parseArgs,
  runCommand,
} from '../../utils';
import path from 'node:path';

const action = async () => {
  const args = parseArgs({ spec: Args.Android, argv: process.argv.slice(2) });
  const config = await getAndroidConfig(args);

  if (config.help) {
    console.log(Help.Android);
    return process.exit(0);
  }

  // TODO: Validate and run prebuild?

  // Build and publish library
  console.log('Building and publishing brownfield and dependencies');
  for (const repository of config.repositories) {
    const task = constructTask(config.buildType, repository);
    console.log(`Publishing to repository: ${repository} (${task})`);
    console.log(
      `Running command: ./gradlew ${task} at ${path.join(process.cwd(), 'android')}`,
    );
    await runCommand('./gradlew', [task], {
      cwd: path.join(process.cwd(), 'android'),
      verbose: config.verbose,
    });
  }
};

export default action;

const constructTask = (
  buildType: BuildTypeAndroid,
  repository: string,
): string => {
  const buildTypeCapitalized = buildType[0].toUpperCase() + buildType.slice(1);
  const repositorySuffixed =
    repository === 'MavenLocal' ? repository : `${repository}Repository`;
  return `publishBrownfield${buildTypeCapitalized}PublicationTo${repositorySuffixed}`;
};
