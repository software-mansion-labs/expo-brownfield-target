import { withAndroidManifest, type ConfigPlugin } from 'expo/config-plugins';
import type { PluginConfig } from '../types';
import { mkdir } from '../../common';
import path from 'node:path';

const withProjectFilesPlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withAndroidManifest(config, (config) => {
    mkdir(path.join(pluginConfig.projectRoot, 'android/brownfield/src'), true);

    return config;
  });
};

export default withProjectFilesPlugin;
