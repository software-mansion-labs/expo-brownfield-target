import fs from 'node:fs/promises';
import { Defaults } from '../constants';

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
