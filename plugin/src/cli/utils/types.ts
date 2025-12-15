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
