import { spawn } from 'node:child_process';
import fs from 'node:fs/promises';
import readline from 'node:readline/promises';
import { Loader } from './output';
import type { RunCommandOptions } from './types';

export const runCommand = (
  command: string,
  args: string[] = [],
  options?: RunCommandOptions,
): Promise<void> => {
  return new Promise((resolve, reject) => {
    const stdio = options?.verbose ? 'inherit' : 'pipe';

    const childProc = spawn(command, args, {
      stdio,
      shell: process.platform === 'win32',
      env: {
        ...process.env,
        ...(options?.env ?? {}),
      },
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
        resolve();
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
      `Project seems to be missing prebuild for ${platform}. Do you want to prebuild now? [Y/n] `,
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
      console.log(`Native project for ${platform} prebuilt successfully`);

      return true;
    } else {
      return false;
    }
  }

  return true;
};

export const getOptionValue = (options: string[], flags: string[]): string => {
  if (!flags.some((flag) => options.includes(flag))) {
    return '';
  }

  const index = options.findLastIndex((option) => flags.includes(option));
  if (index + 1 >= options.length) {
    console.error(
      `Error: Option '${options[index]}' requires a value to be passed`,
    );
    process.exit(1);
  }

  return options[index + 1];
};
