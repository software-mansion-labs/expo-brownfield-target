import type { ConfigPlugin } from 'expo/config-plugins';

import {
  withBuildPropertiesPlugin,
  withPodfilePlugin,
  withPodfilePropertiesPlugin,
  withXcodeProjectPlugin,
} from './plugins';

const withIosPlugin: ConfigPlugin = (config) => {
  config = withXcodeProjectPlugin(config);
  config = withPodfilePlugin(config);
  config = withPodfilePropertiesPlugin(config);
  config = withBuildPropertiesPlugin(config);
  return config;
};

export default withIosPlugin;
