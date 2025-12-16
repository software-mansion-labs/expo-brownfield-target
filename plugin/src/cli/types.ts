export type CLICommand = 'build-ios' | 'build-android';
export type CLIAction = CLICommand | 'help' | 'version' | 'unknownCommand';

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
