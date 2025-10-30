import type { ExpoConfig } from 'expo/config';
import type { PluginConfig, PluginProps } from '../types';
import { getProjectRoot } from './project';

export const getPluginConfig = (
  _props: PluginProps,
  config: ExpoConfig,
): PluginConfig => {
  return {
    projectRoot: getProjectRoot(config),
  };
};
