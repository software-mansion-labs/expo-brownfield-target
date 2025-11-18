import path from 'node:path';

import { type ConfigPlugin, withAndroidManifest } from 'expo/config-plugins';

import { mkdir } from '../../common';
import type { PluginConfig } from '../types';
import { createFileFromTemplate } from '../utils';

const withProjectFilesPlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withAndroidManifest(config, (config) => {
    // Define paths for the brownfield target
    const brownfieldPath = path.join(
      pluginConfig.projectRoot,
      'android/brownfield',
    );
    const brownfieldMainPath = path.join(brownfieldPath, 'src/main/');
    const brownfieldSourcesPath = path.join(
      brownfieldMainPath,
      pluginConfig.packagePath,
    );

    // Define groupId and artifactId by splitting packageId
    const lastDotIndex = pluginConfig.package.lastIndexOf('.');
    const groupId = pluginConfig.package.substring(0, lastDotIndex);
    const artifactId = pluginConfig.package.substring(lastDotIndex + 1);

    // Create directory for brownfield target sources
    // and all intermediate directories
    mkdir(brownfieldSourcesPath, true);

    // Add ReactNativeFragment.kt to the brownfield target:
    // TODO: Consider inclusion of below
    createFileFromTemplate('ReactNativeFragment.kt', brownfieldSourcesPath, {
      packageId: pluginConfig.package,
    });

    // Add files from templates to the brownfield target:
    // - AndroidManifest.xml
    // - ReactNativeHostManager.kt
    // - ReactNativeViewFactory.kt
    // - build.gradle.kts
    // - proguard-rules.pro
    // - consumer-rules.pro
    createFileFromTemplate('AndroidManifest.xml', brownfieldMainPath);
    createFileFromTemplate('ReactNativeHostManager.kt', brownfieldSourcesPath, {
      packageId: pluginConfig.package,
    });
    createFileFromTemplate('ReactNativeViewFactory.kt', brownfieldSourcesPath, {
      packageId: pluginConfig.package,
    });
    createFileFromTemplate('build.gradle.kts', brownfieldPath, {
      packageId: pluginConfig.package,
      groupId,
      artifactId,
    });
    createFileFromTemplate('proguard-rules.pro', brownfieldPath);
    createFileFromTemplate('consumer-rules.pro', brownfieldPath);

    return config;
  });
};

export default withProjectFilesPlugin;
