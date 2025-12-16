import fs from 'node:fs/promises';
import { Args, Help } from '../../constants';
import {
  BuildConfigIos,
  getIosConfig,
  parseArgs,
  printConfig,
  runCommand,
  withSpinner,
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
  return withSpinner({
    operation: async () => {
      try {
        await fs.access(artifactsPath);
      } catch (error) {
        // Ignore if directory does not exist
        return;
      }

      const artifacts = (await fs.readdir(artifactsPath)).filter((artifact) =>
        artifact.endsWith('.xcframework'),
      );

      for (const artifact of artifacts) {
        await fs.rm(`${artifactsPath}/${artifact}`, {
          recursive: true,
          force: true,
        });
      }
    },
    loaderMessage: 'Cleaning up previous artifacts...',
    successMessage: 'Cleaning up previous artifacts succeeded',
    errorMessage: 'Cleaning up previous artifacts failed',
  });
};

const runBuild = async (config: BuildConfigIos) => {
  return withSpinner({
    operation: () =>
      runCommand(
        'xcodebuild',
        [
          '-workspace',
          config.workspace,
          '-scheme',
          config.scheme,
          '-derivedDataPath',
          'ios/build',
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
      ),
    loaderMessage: 'Compiling framework...',
    successMessage: 'Compiling framework succeeded',
    errorMessage: 'Compiling framework failed',
    verbose: config.verbose,
  });
};

const packageFrameworks = async (config: BuildConfigIos) => {
  return withSpinner({
    operation: () =>
      runCommand(
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
      ),
    loaderMessage: 'Packaging framework into an XCFramework...',
    successMessage: 'Packaging framework into an XCFramework succeeded',
    errorMessage: 'Packaging framework into an XCFramework failed',
    verbose: config.verbose,
  });
};

const copyHermesFramework = async (config: BuildConfigIos) => {
  return withSpinner({
    operation: () =>
      fs.cp(
        `./ios/${config.hermesFrameworkPath}`,
        `${config.artifacts}/hermes.xcframework`,
        {
          force: true,
          recursive: true,
        },
      ),
    loaderMessage: 'Copying hermes.xcframework to the artifacts directory...',
    successMessage:
      'Copying hermes.xcframework to the artifacts directory succeeded',
    errorMessage:
      'Copying hermes.xcframework to the artifacts directory failed',
    verbose: config.verbose,
  });
};
