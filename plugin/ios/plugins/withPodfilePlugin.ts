import { type ConfigPlugin, withPodfile } from 'expo/config-plugins';

import {
  addCustomRubyScriptImport,
  addNewPodsTarget,
} from '../utils';
import type { PluginConfig } from '../types';

const withPodfilePlugin: ConfigPlugin<PluginConfig> = (config, pluginConfig) => {
  return withPodfile(config, (config) => {
    config.modResults.contents = addCustomRubyScriptImport(
      config.modResults.contents,
      pluginConfig.targetName,
    );
    config.modResults.contents = addNewPodsTarget(
      config.modResults.contents,
      pluginConfig.targetName,
    );
    return config;
  });
};

export default withPodfilePlugin;
