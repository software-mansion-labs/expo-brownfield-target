export interface PluginConfig {
  package: string;
  packagePath: string;
  projectRoot: string;
}

export type AndroidPluginProps = Omit<
  PluginConfig,
  'projectRoot' | 'packagePath'
>;

export type PluginProps = Partial<AndroidPluginProps> | undefined;
