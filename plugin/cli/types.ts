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

export interface RunCommandOptions {
  verbose?: boolean;
  env?: Record<string, string>;
}
