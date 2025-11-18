export interface PluginConfig {
  libraryName: string;
  package: string;
  packagePath: string;
  projectRoot: string;
}

export type AndroidPluginProps = Pick<PluginConfig, 'libraryName' | 'package'>;

export type PluginProps = Partial<AndroidPluginProps> | undefined;
