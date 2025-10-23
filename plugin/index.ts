import type { ConfigPlugin } from 'expo/config-plugins';

import withAndroidPlugin from './android';
import withIosPlugin from './ios';

const withExpoBrownfieldTargetPlugin: ConfigPlugin = (config) => {
  config = withAndroidPlugin(config);
  return withIosPlugin(config);
};

export default withExpoBrownfieldTargetPlugin;
