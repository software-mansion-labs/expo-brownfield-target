export type CLICommand = 'build-ios' | 'build-android';
export type CLIAction = CLICommand | 'help' | 'version' | 'unknown';

/* Build configuration */
export type BuildType = 'Debug' | 'Release';

export interface BuildConfigCommon {
  artifactsDir: string;
  configuration: BuildType;
  verbose: boolean;
}

export interface BuildConfigIOS extends BuildConfigCommon {
  derivedDataPath: string;
  hermesFrameworkPath: string;
  xcworkspace: string;
  scheme: string;
}

export interface BuildConfigAndroid extends BuildConfigCommon {
  libraryName: string;
  publish: boolean;
  customTasks: string[];
}

export type BasicConfigIOS = Pick<
  BuildConfigIOS,
  'artifactsDir' | 'hermesFrameworkPath'
>;

export type BasicConfigAndroid = Pick<BuildConfigAndroid, 'artifactsDir'>;

export interface RunCommandOptions {
  cwd?: string;
  verbose?: boolean;
  env?: Record<string, string>;
}
