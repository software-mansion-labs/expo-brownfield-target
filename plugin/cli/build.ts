import { spawn } from 'node:child_process';
import fs from 'node:fs/promises';
import readline from 'node:readline/promises';
import { errorMessage, Loader, successMessage, warningMessage } from './output';
import type { BuildConfigCommon, BuildType, RunCommandOptions } from './types';
import chalk from 'chalk';
import path from 'node:path';

export const runCommand = (
  command: string,
  args: string[] = [],
  options?: RunCommandOptions,
  // TODO; Fix return type
): Promise<object> => {
  return new Promise((resolve, reject) => {
    const stdio = options?.verbose ? 'inherit' : 'pipe';

    const childProc = spawn(command, args, {
      stdio,
      shell: process.platform === 'win32',
      env: {
        ...process.env,
        ...(options?.env ?? {}),
      },
      cwd: options?.cwd,
    });

    let stdOut = '';
    let stdErr = '';

    if (!options?.verbose) {
      childProc.stdout?.on('data', (data) => {
        stdOut += data.toString();
      });

      childProc.stderr?.on('data', (chunk) => {
        stdErr += chunk;
      });
    }

    childProc.on('close', (code) => {
      if (code === 0) {
        resolve({ stdout: stdOut });
      } else {
        const errorMessage = `Command '${command} ${args.join(' ')}' failed with code ${code}
        \n${stdErr.substring(0, 300)}`;
        reject(errorMessage);
      }
    });

    childProc.on('error', (error) => {
      reject(error);
    });

    childProc.on('exit', () => {});
  });
};

const hasPrebuildDirectory = async (
  platform: 'ios' | 'android',
): Promise<boolean> => {
  try {
    await fs.access(platform);
    return true;
  } catch (error: unknown) {
    return false;
  }
};

export const validatePrebuild = async (
  platform: 'ios' | 'android',
): Promise<boolean> => {
  if (!(await hasPrebuildDirectory(platform))) {
    const rl = readline.createInterface({
      input: process.stdin,
      output: process.stdout,
    });

    let response = await rl.question(
      `${chalk.yellow(warningMessage)} Project seems to be missing prebuild for ${platform}. Do you want to prebuild now? [Y/n] `,
    );
    response = response.toLowerCase();

    if (response === 'y' || response === 'yes') {
      Loader.shared.start(`Prebuilding native project for ${platform}...`);

      await runCommand(
        'npx',
        ['expo', 'prebuild', '--clean', '--platform', platform],
        {
          env: { EXPO_NO_GIT_STATUS: '1' },
        },
      );

      Loader.shared.stop();
      successMessage(`Native project for ${platform} prebuilt successfully`);

      return true;
    } else {
      errorMessage(`Missing prebuild! Skipping building for ${platform}`);
      return false;
    }
  }

  return true;
};

export const getOptionValue = (options: string[], flags: string[]): string => {
  const index = options.findLastIndex((option) => flags.includes(option));
  if (index === -1) {
    return '';
  }

  // --flag=value
  if (options[index].includes('=')) {
    return options[index].split('=')[1];
  }

  // --flag value
  if (index + 1 >= options.length) {
    errorMessage(`Option '${options[index]}' requires a value to be passed`);
    process.exit(1);
  }

  return options[index + 1];
};

export const splitOptionList = (optionValue: string): string[] => {
  if (!optionValue) {
    return [];
  }

  return optionValue.split(',');
};

export const getCommonConfig = async (
  options: string[],
): Promise<BuildConfigCommon> => {
  // TODO: Change?
  // Hardcoded for now
  const artifactsDir = path.join(process.cwd(), 'artifacts');

  let configuration: BuildType = 'Release';
  if (options.includes('-d') || options.includes('--debug')) {
    configuration = 'Debug';
  }
  // If both options are passed, release takes precedence
  if (options.includes('-r') || options.includes('--release')) {
    configuration = 'Release';
  }

  let verbose = false;
  if (options.includes('--verbose')) {
    verbose = true;
  }

  return {
    artifactsDir,
    configuration,
    verbose,
  };
};
