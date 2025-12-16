import type { Spec } from 'arg';

export interface HelpMessageCommand {
  command: string;
  description: string;
  hasOptions?: boolean;
}

export interface HelpMessageOption {
  description: string;
  option: string;
  short?: string;
}

export interface HelpMessageParams {
  commands?: HelpMessageCommand[];
  options?: HelpMessageOption[];
  promptCommand?: string;
  promptOptions?: string;
}

export interface HelpMessageSectionParams<T> {
  items?: T[];
  left: (item: T) => string;
  right: (item: T) => string;
  title: string;
}

export interface ParseArgsParams {
  spec: Spec;
  argv?: string[];
  stopAtPositional?: boolean;
}

export type BuildTypeCommon = 'debug' | 'release';

export type BuildTypeAndroid = BuildTypeCommon | 'all';

export interface BuildConfigCommon {
  help: boolean;
  verbose: boolean;
}

export interface BuildConfigAndroid extends BuildConfigCommon {
  buildType: BuildTypeAndroid;
  libraryName: string;
  repositories: string[];
  tasks: string[];
}

export interface BuildConfigIos extends BuildConfigCommon {
  artifacts: string;
  hermesFrameworkPath: string;
  buildType: BuildTypeCommon;
  scheme: string;
  workspace: string;
}

export interface RunCommandOptions {
  cwd?: string;
  env?: Record<string, string>;
  verbose?: boolean;
}

export interface RunCommandResult {
  stdout: string;
}
