import type { ExpoConfig } from 'expo/config';
import type { PluginConfig, PluginProps } from '../types';

export const getPluginConfig = (
  props: PluginProps,
  config: ExpoConfig,
): PluginConfig => {
  // Brownfield target name
  // Default value: <config-scheme>brownfield
  const targetName = props?.targetName ?? config.scheme + 'brownfield';

  return {
    targetName,
  };
};
