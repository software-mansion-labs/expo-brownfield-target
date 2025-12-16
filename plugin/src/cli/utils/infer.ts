import fs from 'node:fs/promises';
import { Defaults } from '../constants';
import path from 'node:path';

export const inferAndroidLibrary = async (): Promise<string> => {
  const files = ['ReactNativeFragment.kt', 'ReactNativeHostManager.kt'];

  try {
    const android = await fs.readdir('android', { withFileTypes: true });
    const directories = android.filter((item) => item.isDirectory());
    for (const directory of directories) {
      const contents = await fs.readdir(`android/${directory.name}`, {
        recursive: true,
      });

      const hasAllFiles = files.every((file) =>
        contents.find((item) => item.includes(file)),
      );

      if (hasAllFiles) {
        return directory.name;
      }
    }

    return Defaults.libraryName;
  } catch (error) {
    // TODO: Handle error
    return process.exit(1);
  }
};

export const inferXCWorkspace = async (): Promise<string> => {
  try {
    const xcworkspace = (await fs.readdir('ios', { withFileTypes: true })).find(
      (item) => item.name.endsWith('.xcworkspace'),
    );
    if (xcworkspace) {
      return path.join(xcworkspace.parentPath, xcworkspace.name);
    }

    // TODO: Handle error
    return process.exit(1);
  } catch (error) {
    // TODO: Handle error
    return process.exit(1);
  }
};

export const inferScheme = async (): Promise<string> => {
  try {
    const subDirs = (await fs.readdir('ios', { withFileTypes: true })).filter(
      (item) => item.isDirectory(),
    );
    let scheme: string | undefined = undefined;
    for (const subDir of subDirs) {
      // TODO: Rename this file to RNHostManager?
      if ((await fs.readdir(`ios/${subDir.name}`)).includes('ExpoApp.swift')) {
        scheme = subDir.name;
      }
    }

    if (scheme) {
      return scheme;
    }

    // TODO: Handle error
    return process.exit(1);
  } catch (error) {
    // TODO: Handle error
    return process.exit(1);
  }
};
