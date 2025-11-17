export type CLICommand = 'build-ios' | 'build-android';
export type CLIAction = CLICommand | 'help' | 'version' | 'unknown';

export interface BuildConfig {
  android?: boolean;
  help?: boolean;
  ios?: boolean;
}

export type ConfigurationIOS = 'Debug' | 'Release';

export interface BuildConfigIOS {
  artifactsDir: string;
  derivedDataPath: string;
  hermesFrameworkPath: string;
  xcworkspace: string;
  scheme: string;
  configuration: ConfigurationIOS;
}

export type BasicConfigIOS = Pick<
  BuildConfigIOS,
  'artifactsDir' | 'hermesFrameworkPath'
>;

export interface BuildConfigAndroid {
  artifactsDir: string;
  libraryName: string;
  configuration: ConfigurationIOS;
  publish: boolean;
  customTasks: string[];
  verbose: boolean;
}

export type BasicConfigAndroid = Pick<BuildConfigAndroid, 'artifactsDir'>;

export interface RunCommandOptions {
  cwd?: string;
  verbose?: boolean;
  env?: Record<string, string>;
}
