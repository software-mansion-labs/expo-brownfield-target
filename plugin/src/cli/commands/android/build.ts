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

  let tasks = [];
  if (config.tasks.length > 0) {
    tasks = config.tasks;
  } else {
    for (const repository of config.repositories) {
      const task = constructTask(config.buildType, repository);
      tasks.push(task);
    }
  }

  for (const task of tasks) {
    await runTask(task, config.verbose);
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

const runTask = async (task: string, verbose: boolean) => {
  await runCommand('./gradlew', [task], {
    cwd: path.join(process.cwd(), 'android'),
    verbose: verbose,
  });
};
