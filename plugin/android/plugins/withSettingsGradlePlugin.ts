import { type ConfigPlugin, withSettingsGradle } from 'expo/config-plugins';

const withSettingsGradlePlugin: ConfigPlugin = (config) => {
  return withSettingsGradle(config, (config) => {
    config.modResults.contents += "include ':brownfield'\n";
    return config;
  });
};

export default withSettingsGradlePlugin;
