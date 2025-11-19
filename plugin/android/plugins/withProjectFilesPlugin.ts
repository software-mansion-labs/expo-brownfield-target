import { withAndroidManifest, type ConfigPlugin } from 'expo/config-plugins';
import type { PluginConfig } from '../types';
import {
  createFileFromTemplate,
  createFileFromTemplateAs,
  mkdir,
} from '../../common';
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
    // TODO: Consider placement for below
    // createFileFromTemplate(
    //   'ReactNativeFragment.kt',
    //   path.join(
    //     pluginConfig.projectRoot,
    //     'android/brownfield/src/main/',
    //     packagePath,
    //   ),
    //   'android',
    // );

    // Add build.gradle.kts file
    createFileFromTemplate(
      'build.gradle.kts',
      path.join(pluginConfig.projectRoot, 'android/brownfield'),
      'android',
    );

    // Add proguard-rules.pro file
    createFileFromTemplate(
      'proguard-rules.pro',
      path.join(pluginConfig.projectRoot, 'android/brownfield'),
      'android',
    );

    // Add consumer-rules.pro file
    createFileFromTemplate(
      'consumer-rules.pro',
      path.join(pluginConfig.projectRoot, 'android/brownfield'),
      'android',
    );

    // Add buildSrc directory
    mkdir(
      path.join(pluginConfig.projectRoot, 'android/buildSrc/src/main'),
      true,
    );

    // Add gradle-plugins directory
    mkdir(
      path.join(
        pluginConfig.projectRoot,
        'android/buildSrc/src/main/resources/META-INF/gradle-plugins',
      ),
      true,
    );

    // Add gradle-plugin.properties file
    createFileFromTemplateAs(
      'plugin.properties',
      path.join(
        pluginConfig.projectRoot,
        'android/buildSrc/src/main/resources/META-INF/gradle-plugins',
      ),
      'com.pmleczek.expo-brownfield.properties',
      'android',
    );

    // Add ExpoBrownfieldPlugin.kt file
    mkdir(
      path.join(
        pluginConfig.projectRoot,
        'android/buildSrc/src/main/kotlin/com/pmleczek/plugin',
      ),
      true,
    );
    createFileFromTemplateAs(
      'Plugin.kt',
      path.join(
        pluginConfig.projectRoot,
        'android/buildSrc/src/main/kotlin/com/pmleczek/plugin',
      ),
      'ExpoBrownfieldPlugin.kt',
      'android',
    );

    // Add build.gradle.kts file for the plugin
    console.log(path.join(pluginConfig.projectRoot, 'android/buildSrc'));
    createFileFromTemplateAs(
      'build.gradle.plugin.kts',
      path.join(pluginConfig.projectRoot, 'android/buildSrc'),
      'build.gradle.kts',
      'android',
    );

    return config;
  });
};

export default withProjectFilesPlugin;
