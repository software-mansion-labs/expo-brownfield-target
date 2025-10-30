import { withAndroidManifest, type ConfigPlugin } from 'expo/config-plugins';
import type { PluginConfig } from '../types';
import { createFileFromTemplate, mkdir } from '../../common';
import path from 'node:path';
import { getPackagePath } from '../utils';

const withProjectFilesPlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withAndroidManifest(config, (config) => {
    const packagePath = getPackagePath(pluginConfig.projectRoot);

    // Create directory for the brownfield library
    // and for the source code of the brownfield
    mkdir(
      path.join(
        pluginConfig.projectRoot,
        'android/brownfield/src/main/',
        packagePath,
      ),
      true,
    );

    // Create AndroidManifest.xml file from the template
    createFileFromTemplate(
      'AndroidManifest.xml',
      path.join(pluginConfig.projectRoot, 'android/brownfield/src/main/'),
      'android',
    );

    // Create ReactNativeHostManager.kt from the template
    createFileFromTemplate(
      'ReactNativeHostManager.kt',
      path.join(
        pluginConfig.projectRoot,
        'android/brownfield/src/main/',
        packagePath,
      ),
      'android',
    );

    // Create ReactNativeViewFactory.kt from the template
    createFileFromTemplate(
      'ReactNativeViewFactory.kt',
      path.join(
        pluginConfig.projectRoot,
        'android/brownfield/src/main/',
        packagePath,
      ),
      'android',
    );

    // Create ReactNativeFragment.kt from the template
    createFileFromTemplate(
      'ReactNativeFragment.kt',
      path.join(
        pluginConfig.projectRoot,
        'android/brownfield/src/main/',
        packagePath,
      ),
      'android',
    );

    // Add build.gradle file
    createFileFromTemplate(
      'build.gradle',
      path.join(pluginConfig.projectRoot, 'android/brownfield'),
      'android',
    );

    return config;
  });
};

export default withProjectFilesPlugin;
