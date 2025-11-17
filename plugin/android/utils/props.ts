import type { ExpoConfig } from 'expo/config';
import path from 'node:path';
import type { PluginConfig, PluginProps } from '../types';
import { withAndroidManifest } from 'expo/config-plugins';

export const getPluginConfig = (
  props: PluginProps,
  config: ExpoConfig,
): PluginConfig => {
  const packageId = getPackage(config, props);

  return {
    package: packageId,
    packagePath: getPackagePath(packageId),
    projectRoot: getProjectRoot(config),
  };
};

const getPackage = (config: ExpoConfig, props: PluginProps): string => {
  if (props?.package) {
    return props.package;
  }

  if (config.android?.package) {
    return config.android.package + '.brownfield';
  }

  return 'com.example.brownfield';
};

export const getPackagePath = (packageId: string) => {
  return path.join('java', ...packageId.split('.'));
};

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
