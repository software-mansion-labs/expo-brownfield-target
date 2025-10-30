import { withSettingsGradle, type ConfigPlugin } from 'expo/config-plugins';
// import type { PluginConfig } from '../types';

const withSettingsGradlePlugin: ConfigPlugin = (
  config,
  // pluginConfig,
) => {
  return withSettingsGradle(config, (config) => {
    config.modResults.contents += "include ':brownfield'\n";
    return config;
  });
};

export default withSettingsGradlePlugin;
