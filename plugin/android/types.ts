export interface PluginConfig {
  projectRoot: string;
}

export type AndroidPluginProps = Omit<PluginConfig, "projectRoot">;

export type PluginProps = AndroidPluginProps | undefined;