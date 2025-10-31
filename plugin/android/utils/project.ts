import type { ExpoConfig } from 'expo/config';
import { withAndroidManifest } from 'expo/config-plugins';
import { readdirSync } from 'node:fs';
import path from 'node:path';

export const getProjectRoot = (config: ExpoConfig): string => {
  let projectRoot = '';
  withAndroidManifest(config, (config) => {
    projectRoot = config.modRequest.projectRoot;
    return config;
  });

  if (!projectRoot && config._internal?.projectRoot) {
    projectRoot = config._internal.projectRoot as string;
  }

  return projectRoot;
};

export const getPackagePath = (projectRoot: string) => {
  const expoAppPath = path.join(projectRoot, 'android', 'app/src/main');
  const expoAppContents = readdirSync(expoAppPath, { recursive: true });

  const mainApplicationFile = expoAppContents.find(
    (path) => typeof path === 'string' && path.includes('MainApplication.'),
  );
  if (!mainApplicationFile || typeof mainApplicationFile !== 'string') {
    throw new Error();
  }

  const stripped = stripPackagePath(mainApplicationFile);

  return path.join(stripped, 'brownfield');
};

const stripPackagePath = (mainApplicationPath: string): string => {
  return mainApplicationPath.replace(/\/MainApplication.*/, '');
};
