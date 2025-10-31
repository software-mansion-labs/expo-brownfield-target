import type { ConfigPlugin } from 'expo/config-plugins';
import {
  withProjectBuildGradlePlugin,
  withProjectFilesPlugin,
  withSettingsGradlePlugin,
} from './plugins';
import { getPluginConfig } from './utils';
import type { PluginProps } from './types';

const withAndroidPlugin: ConfigPlugin<PluginProps> = (config, props) => {
  const pluginConfig = getPluginConfig(props, config);

  config = withProjectFilesPlugin(config, pluginConfig);
  config = withSettingsGradlePlugin(config);
  config = withProjectBuildGradlePlugin(config);

  return config;
};

export default withAndroidPlugin;
