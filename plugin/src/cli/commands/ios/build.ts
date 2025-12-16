import ora, { Ora } from 'ora';
import chalk from 'chalk';
import fs from 'node:fs/promises';
import { Args, Help } from '../../constants';
import {
  BuildConfigIos,
  getIosConfig,
  parseArgs,
  printConfig,
  runCommand,
} from '../../utils';

const action = async () => {
  const args = parseArgs({
    spec: Args.IOS,
    argv: process.argv.slice(2),
  });
  const config = await getIosConfig(args);

  if (config.help) {
    console.log(Help.IOS);
    return process.exit(0);
  }

  // TODO: Validate and run prebuild?

  printConfig(config);
  await cleanUpArtifacts(config.artifacts);
  await runBuild(config);
  await packageFrameworks(config);
  await copyHermesFramework(config);
};

export default action;

const cleanUpArtifacts = async (artifactsPath: string) => {
  try {
    await fs.access(artifactsPath);
    const artifacts = (await fs.readdir(artifactsPath)).filter((artifact) =>
      artifact.endsWith('.xcframework'),
    );

    for (const artifact of artifacts) {
      await fs.rm(`${artifactsPath}/${artifact}`, {
        recursive: true,
        force: true,
      });
    }
  } catch (error) {
    // TODO: Handle error - should continue
    console.error(error);
  }
};

const runBuild = async (config: BuildConfigIos) => {
  // TODO: Extract spinners to some common function?
  let spinner: Ora | undefined;
  try {
    if (!config.verbose)
      spinner = ora('Compiling brownfield framework...').start();
    await runCommand(
      'xcodebuild',
      [
        '-workspace',
        config.workspace,
        '-scheme',
        config.scheme,
        '-derivedDataPath',
        `ios/build`,
        '-destination',
        'generic/platform=iphoneos',
        '-destination',
        'generic/platform=iphonesimulator',
        '-configuration',
        config.buildType,
      ],
      {
        verbose: config.verbose,
      },
    );
    if (!config.verbose)
      spinner?.succeed('Compiling brownfield framework succeeded');
  } catch (error) {
    // TODO: Handle error
    console.error(error);
    return process.exit(1);
  } finally {
    if (!config.verbose) spinner?.stop();
  }
};

const packageFrameworks = async (config: BuildConfigIos) => {
  let spinner: Ora | undefined;
  try {
    if (!config.verbose)
      spinner = ora('Packaging brownfield framework...').start();
    await runCommand(
      'xcodebuild',
      [
        '-create-xcframework',
        '-framework',
        `ios/build/Build/Products/${config.buildType}-iphoneos/${config.scheme}.framework`,
        '-framework',
        `ios/build/Build/Products/${config.buildType}-iphonesimulator/${config.scheme}.framework`,
        '-output',
        `${config.artifacts}/${config.scheme}.xcframework`,
      ],
      {
        verbose: config.verbose,
      },
    );
  } catch (error) {
    // TODO: Handle error
    console.error(error);
    return process.exit(1);
  } finally {
    if (!config.verbose) spinner?.stop();
  }
};

const copyHermesFramework = async (config: BuildConfigIos) => {
  let spinner: Ora | undefined;
  if (!config.verbose)
    spinner = ora(
      'Copying hermes.xcframework to the artifacts directory...',
    ).start();
  await fs.cp(
    `./ios/${config.hermesFrameworkPath}`,
    `${config.artifacts}/hermes.xcframework`,
    {
      recursive: true,
    },
  );
  if (!config.verbose)
    spinner?.succeed(
      'Successfully copied hermes.xcframework to the artifact directory',
    );
};
