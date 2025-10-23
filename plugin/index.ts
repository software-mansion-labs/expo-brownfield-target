import type { ConfigPlugin } from 'expo/config-plugins';

import withAndroidPlugin from './android';
import withIosPlugin from './ios';
import type { PluginProps } from './types';

const withExpoBrownfieldTargetPlugin: ConfigPlugin<PluginProps> = (config, props) => {
  config = withAndroidPlugin(config);
  return withIosPlugin(config, props?.ios);
};

export default withExpoBrownfieldTargetPlugin;
