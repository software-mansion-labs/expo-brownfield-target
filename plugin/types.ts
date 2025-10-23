import type { IOSPluginProps } from './ios';

export interface PluginPropsType {
  ios?: IOSPluginProps;
}

export type PluginProps = PluginPropsType | undefined;
