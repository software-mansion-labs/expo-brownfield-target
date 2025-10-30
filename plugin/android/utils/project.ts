import type { ExpoConfig } from 'expo/config';
import { withAndroidManifest } from 'expo/config-plugins';

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
