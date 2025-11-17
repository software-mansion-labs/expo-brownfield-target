import fs from 'node:fs/promises';
import type { BasicConfigIOS, BuildConfigIOS } from './types';
import {
  getCommonConfig,
  getOptionValue,
  runCommand,
  validatePrebuild,
} from './build';
import { errorMessage, infoMessage, Loader, successMessage } from './output';
import { BUILD_IOS_HELP_MESSAGE } from './messages';

const basicConfig = {
  artifactsDir: './artifacts',
  derivedDataPath: './ios/build',
  hermesFrameworkPath:
    'Pods/hermes-engine/destroot/Library/Frameworks/universal/hermes.xcframework',
};

const maybeDisplayHelp = (options: string[]) => {
  if (options.includes('-h') || options.includes('--help')) {
    console.log(BUILD_IOS_HELP_MESSAGE);
    process.exit(0);
  }
};

const inferXCWorkspace = async (): Promise<string> => {
  const xcworkspace = (await fs.readdir('ios')).find((item) =>
    item.endsWith('.xcworkspace'),
  );
  if (xcworkspace) {
    return xcworkspace;
  }

  errorMessage(
    'Failed to infer .xcworkspace path from the prebuilt ios directory',
  );
  process.exit(1);
};

const inferScheme = async (): Promise<string> => {
  const subDirs = (await fs.readdir('ios', { withFileTypes: true })).filter(
    (item) => item.isDirectory(),
  );
  let scheme: string | undefined = undefined;
  for (let subDir of subDirs) {
    if ((await fs.readdir(`ios/${subDir.name}`)).includes('ExpoApp.swift')) {
      scheme = subDir.name;
    }
  }

  if (scheme) {
    return scheme;
  }

  errorMessage('Failed to infer scheme from the prebuilt ios directory');
  process.exit(1);
};

const getFullConfig = async (options: string[]): Promise<BuildConfigIOS> => {
  const commonConfig = await getCommonConfig(options);

  let xcworkspace = getOptionValue(options, ['-x', '--xcworkspace']);
  if (!xcworkspace) {
    xcworkspace = await inferXCWorkspace();
  }

  let scheme = getOptionValue(options, ['-s', '--scheme']);
  if (!scheme) {
    scheme = await inferScheme();
  }

  return {
    ...basicConfig,
    ...commonConfig,
    scheme,
    xcworkspace,
  };
};

const cleanUpArtifacts = async (config: BasicConfigIOS) => {
  try {
    await fs.access(config.artifactsDir);
    const iosArtifacts = (await fs.readdir(config.artifactsDir)).filter(
      (artifact) => artifact.endsWith('.xcframework'),
    );
    for (const artifact of iosArtifacts) {
      await fs.rm(`${config.artifactsDir}/${artifact}`, {
        recursive: true,
        force: true,
      });
    }
    successMessage(
      `Cleaned up previous iOS artifacts at: ${config.artifactsDir}`,
    );
  } catch (error: unknown) {}
};

const compileFrameworks = async (config: BuildConfigIOS) => {
  Loader.shared.start(
    `Compiling frameworks from scheme: ${config.scheme} with configuration: ${config.configuration}...`,
  );
  await runCommand(
    'xcodebuild',
    [
      '-workspace',
      `ios/${config.xcworkspace}`,
      '-scheme',
      config.scheme,
      '-derivedDataPath',
      config.derivedDataPath,
      '-destination',
      'generic/platform=iphoneos',
      '-destination',
      'generic/platform=iphonesimulator',
      '-configuration',
      config.configuration,
    ],
    {
      verbose: config.verbose,
    },
  );
  Loader.shared.stop();
  successMessage(
    `Successfully compiled frameworks from scheme: ${config.scheme}`,
  );
};

const packageFrameworks = async (config: BuildConfigIOS) => {
  Loader.shared.start('Packaging frameworks into an XCFramework...');
  await runCommand(
    'xcodebuild',
    [
      '-create-xcframework',
      '-framework',
      `${config.derivedDataPath}/Build/Products/Release-iphoneos/${config.scheme}.framework`,
      '-framework',
      `${config.derivedDataPath}/Build/Products/Release-iphonesimulator/${config.scheme}.framework`,
      '-output',
      `${config.artifactsDir}/${config.scheme}.xcframework`,
    ],
    {
      verbose: config.verbose,
    },
  );
  Loader.shared.stop();
  successMessage(
    `Successfully created ${config.scheme}.xcframework at: ${config.artifactsDir}`,
  );
};

const copyHermesFramework = async (config: BasicConfigIOS) => {
  Loader.shared.start(
    'Copying hermes.xcframework to the artifacts directory...',
  );
  await fs.cp(
    `./ios/${config.hermesFrameworkPath}`,
    `${config.artifactsDir}/hermes.xcframework`,
    {
      recursive: true,
    },
  );
  Loader.shared.stop();
  successMessage(
    'Successfully copied hermes.xcframework to the artifact directory',
  );
};

export const buildIOS = async (options: string[]) => {
  infoMessage('Building brownfield for iOS');

  // Show help message
  maybeDisplayHelp(options);

  // Clean up previous build artifacts
  await cleanUpArtifacts(basicConfig);

  // Validate prebuild
  if (!(await validatePrebuild('ios'))) {
    errorMessage(
      'Prebuild validation failed. Please run `npx expo prebuild --platofm ios` manually and try again.',
    );
    return;
  }
  successMessage('Prebuild validated successfully');

  // Validate or infer configurations
  const config = await getFullConfig(options);

  // Compile frameworks
  await compileFrameworks(config);

  // Package as .xcframework
  await packageFrameworks(config);

  // Copy hermes.xcframework
  await copyHermesFramework(basicConfig);
};
