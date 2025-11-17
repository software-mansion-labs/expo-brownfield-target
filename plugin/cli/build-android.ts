import fs from 'node:fs/promises';
import path from 'node:path';

import {
  getCommonConfig,
  getOptionValue,
  runCommand,
  splitOptionList,
  validatePrebuild,
} from './build';
import { BUILD_ANDROID_HELP_MESSAGE } from './messages';
import { errorMessage, infoMessage, Loader, successMessage } from './output';
import type { BuildConfigAndroid, BuildConfigCommon } from './types';

const maybeDisplayHelp = (options: string[]) => {
  if (options.includes('-h') || options.includes('--help')) {
    console.log(BUILD_ANDROID_HELP_MESSAGE);
    process.exit(0);
  }
};

const getFullConfig = async (
  options: string[],
  commonConfig: BuildConfigCommon,
): Promise<BuildConfigAndroid> => {
  let publish = true;
  if (options.includes('--no-publish')) {
    publish = false;
  }

  // TODO: Change?
  // Hardcoded for now
  const libraryName = 'brownfield';

  const customTasks = splitOptionList(
    getOptionValue(options, ['--tasks', '-t']),
  );

  return {
    ...commonConfig,
    libraryName,
    publish,
    customTasks,
  };
};

const cleanUpArtifacts = async (config: BuildConfigCommon) => {
  try {
    await fs.access(config.artifactsDir);
    const androidArtifacts = (await fs.readdir(config.artifactsDir)).filter(
      (artifact) => artifact.endsWith('.aar'),
    );
    for (const artifact of androidArtifacts) {
      await fs.rm(`${config.artifactsDir}/${artifact}`, {
        recursive: true,
        force: true,
      });
    }
    successMessage(
      `Cleaned up previous Android artifacts at: ${config.artifactsDir}`,
    );
    // eslint-disable-next-line no-empty
  } catch {}
};

const compileLibrary = async (config: BuildConfigAndroid) => {
  console.log(`Compiling :${config.libraryName} library...`);
  const androidPath = path.join(process.cwd(), 'android');

  // Run ./gradlew clean on the target
  const cleanTask = `:${config.libraryName}:clean`;
  Loader.shared.start(`Running ${cleanTask}...`);
  await runCommand('./gradlew', [cleanTask], {
    cwd: androidPath,
    verbose: config.verbose,
  });
  Loader.shared.stop();
  successMessage(`Successfully ran ${cleanTask}...`);

  // Run ./gradlew assemble<configuration> on the target
  const gradlewTask = `:${config.libraryName}:assemble${config.configuration}`;
  Loader.shared.start(`Running ${gradlewTask}...`);
  await runCommand('./gradlew', [gradlewTask], {
    cwd: androidPath,
    verbose: config.verbose,
  });
  Loader.shared.stop();
  successMessage(`Successfully ran ${gradlewTask}...`);
};

const copyAARToArtifacts = async (config: BuildConfigAndroid) => {
  Loader.shared.start(`Copying AAR to artifacts directory...`);
  const androidPath = path.join(process.cwd(), 'android');
  const aarPath = path.join(
    androidPath,
    `${config.libraryName}/build/outputs/aar/${config.libraryName}-${config.configuration.toLowerCase()}.aar`,
  );
  await fs.cp(
    aarPath,
    path.join(
      config.artifactsDir,
      `${config.libraryName}-${config.configuration.toLowerCase()}.aar`,
    ),
    {
      recursive: true,
    },
  );
  Loader.shared.stop();
  successMessage(`Successfully copied AAR to the artifacts directory`);
};

const maybePublishAAR = async (config: BuildConfigAndroid) => {
  if (!config.publish) {
    infoMessage(
      '--no-publish flag passed, skipping publication of AAR to Maven',
    );
    return;
  }

  const androidPath = path.join(process.cwd(), 'android');

  const { stdout } = await runCommand(
    './gradlew',
    [`:${config.libraryName}:tasks`],
    {
      cwd: androidPath,
    },
  );
  const hasDefaultPublishTask = stdout.includes(
    'publishMavenAarPublicationToMavenLocal',
  );
  if (!hasDefaultPublishTask) {
    errorMessage(
      `Default publish task: \`publishMavenAarPublicationToMavenLocal\` not found in the project ${config.libraryName}`,
    );
    errorMessage('Skipping publication of AAR to Maven');
    return;
  }

  const publishTask = `:${config.libraryName}:publishMavenAarPublicationToMavenLocal`;
  Loader.shared.start(`Running ${publishTask}...`);
  await runCommand('./gradlew', [publishTask], {
    cwd: androidPath,
    verbose: config.verbose,
  });
  Loader.shared.stop();
  successMessage(`Successfully published AAR to local Maven repo`);
};

const maybeRunCustomTasks = async (config: BuildConfigAndroid) => {
  if (!config.customTasks || config.customTasks.length === 0) {
    return;
  }

  const androidPath = path.join(process.cwd(), 'android');
  infoMessage(`Running custom tasks`);

  for (const task of config.customTasks) {
    const taskCommand = `:${config.libraryName}:${task}`;
    Loader.shared.start(`Running ${taskCommand}...`);
    await runCommand('./gradlew', [taskCommand], {
      cwd: androidPath,
      verbose: config.verbose,
    });
    Loader.shared.stop();
    successMessage(`Successfully ran ${taskCommand}...`);
  }
};

export const buildAndroid = async (options: string[]) => {
  infoMessage('Building brownfield for Android');
  const commonConfig = await getCommonConfig(options);

  // Show help message
  maybeDisplayHelp(options);

  // Clean up previous build artifacts
  await cleanUpArtifacts(commonConfig);

  // Validate prebuild
  infoMessage('Validating prebuild for Android...');
  if (!(await validatePrebuild('android'))) {
    errorMessage(
      'Prebuild validation failed. Please run `npx expo prebuild --platofm android` manually and try again.',
    );
    return;
  }
  successMessage('Prebuild validated successfully');

  // Validate or infer configurations
  const config = await getFullConfig(options, commonConfig);

  // Compile the fat-AAR
  await compileLibrary(config);

  // Copy the AAR to the artifacts directory
  await copyAARToArtifacts(config);

  // Publish the AAR to Maven
  await maybePublishAAR(config);

  // Run custom tasks
  await maybeRunCustomTasks(config);
};
