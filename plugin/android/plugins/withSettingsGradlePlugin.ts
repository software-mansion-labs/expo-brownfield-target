import { type ConfigPlugin, withSettingsGradle } from 'expo/config-plugins';

import type { PluginConfig } from '../types';

const withSettingsGradlePlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withSettingsGradle(config, (config) => {
    config.modResults.contents += `include ':${pluginConfig.libraryName}'\n`;
    return config;
  });
};

export default withSettingsGradlePlugin;
