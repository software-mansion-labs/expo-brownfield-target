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

/* Command execution */
export interface RunCommandOptions {
  cwd?: string;
  env?: Record<string, string>;
  verbose?: boolean;
}

export interface RunCommandResult {
  stdout: string;
}
