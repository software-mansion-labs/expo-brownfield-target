import type { ConfigPlugin } from 'expo/config-plugins';
import { withProjectFilesPlugin } from './plugins';
import { getPluginConfig } from './utils';
import type { PluginProps } from './types';

const withAndroidPlugin: ConfigPlugin<PluginProps> = (config, props) => {
  const pluginConfig = getPluginConfig(props, config);

  config = withProjectFilesPlugin(config, pluginConfig);

  return config;
};

export default withAndroidPlugin;
