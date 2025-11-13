export type CLIAction = 'help' | 'version' | 'build' | 'unknown';

export interface BuildConfig {
  android?: boolean;
  help?: boolean;
  ios?: boolean;
}
