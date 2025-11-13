import fs from 'node:fs/promises';
import type { BasicConfigIOS, BuildConfigIOS, ConfigurationIOS } from './types';
import { getOptionValue, runCommand, validatePrebuild } from './build';
import { Loader } from './output';

const basicConfig = {
  artifactsDir: './artifacts',
  derivedDataPath: './ios/build',
  hermesFrameworkPath:
    'Pods/hermes-engine/destroot/Library/Frameworks/universal/hermes.xcframework',
};

const inferXCWorkspace = async (): Promise<string> => {
  const xcworkspace = (await fs.readdir('ios')).find((item) =>
    item.endsWith('.xcworkspace'),
  );
  if (xcworkspace) {
    return xcworkspace;
  }

  console.log(
    'Error: Failed to infer .xcworkspace in the prebuilt ios directory',
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

  console.log('Error: Failed to infer scheme in the prebuilt ios directory');
  process.exit(1);
};

const getFullConfig = async (options: string[]): Promise<BuildConfigIOS> => {
  let configuration: ConfigurationIOS = 'Release';
  if (options.includes('-d') || options.includes('--debug')) {
    configuration = 'Debug';
  }
  // If both options are passed, release takes precedence
  if (options.includes('-r') || options.includes('--release')) {
    configuration = 'Release';
  }

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
    configuration,
    scheme,
    xcworkspace,
  };
};

const cleanUpArtifacts = async (config: BasicConfigIOS) => {
  try {
    await fs.access(config.artifactsDir);
    console.log(`i Cleaning up previous artifacts at ${config.artifactsDir}`);
    await fs.rm(config.artifactsDir, { recursive: true, force: true });
  } catch (error: unknown) {}
};

const compileFrameworks = async (config: BuildConfigIOS) => {
  Loader.shared.start('Compiling frameworks...');
  await runCommand('xcodebuild', [
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
  ]);
  Loader.shared.stop();
};

const packageFrameworks = async (config: BuildConfigIOS) => {
  Loader.shared.start('Packaging frameworks into an XCFramework...');
  await runCommand('xcodebuild', [
    '-create-xcframework',
    '-framework',
    `${config.derivedDataPath}/Build/Products/Release-iphoneos/${config.scheme}.framework`,
    '-framework',
    `${config.derivedDataPath}/Build/Products/Release-iphonesimulator/${config.scheme}.framework`,
    '-output',
    `${config.artifactsDir}/${config.scheme}.xcframework`,
  ]);
  Loader.shared.stop();
};

const copyHermesFramework = async (config: BasicConfigIOS) => {
  await fs.cp(`./ios/${config.hermesFrameworkPath}`, `${config.artifactsDir}/hermes.xcframework`, {
    recursive: true,
  });
};

export const buildIOS = async (options: string[]) => {
  console.log('Building brownfield for iOS');

  // Clean up previous build artifacts
  await cleanUpArtifacts(basicConfig);

  // Validate prebuild
  if (!(await validatePrebuild('ios'))) {
    return;
  }

  // Validate or infer configurations
  const config = await getFullConfig(options);

  // Compile frameworks
  await compileFrameworks(config);

  // Package as .xcframework
  await packageFrameworks(config);

  // Copy hermes.xcframework
  await copyHermesFramework(basicConfig);
};
