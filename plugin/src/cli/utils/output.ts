import chalk from 'chalk';
import type { HelpMessageParams } from './types';

// Number of spaces to use for spacing between command/option and description
const SPACING = 30;

export const helpMessage = ({
  commands,
  options,
  promptCommand = '<command>',
  promptOptions = '<options>',
}: HelpMessageParams): string => {
  let optionsSection = '';
  if (options) {
    options.forEach((optionEntry) => {
      const { option, description, short } = optionEntry;

      let optionLine = '';
      optionLine += `  ${option}`;

      if (short) {
        optionLine += `, ${short}`;
      }

      const spacing = SPACING - optionLine.length;
      optionLine += `${' '.repeat(spacing)}${description}\n`;

      optionsSection += `${optionLine}`;
    });
  }

  let commandsSection = '';
  if (commands) {
    commands.forEach((commandEntry) => {
      const { command, description, hasOptions } = commandEntry;

      let commandLine = '';
      commandLine += `  ${command}`;

      if (hasOptions) {
        commandLine += ` [${promptOptions}]`;
      }

      const spacing = SPACING - commandLine.length;
      commandLine += `${' '.repeat(spacing)}${description}\n`;

      commandsSection += `${commandLine}`;
    });
  }

  // TODO: Cleanup
  return `
${chalk.bold('Usage:')} expo-brownfield-target ${promptCommand} [${promptOptions}]
${optionsSection ? `\n${chalk.bold('Options:')}\n${optionsSection.trimEnd()}` : ''}
${commandsSection ? `\n${chalk.bold('Commands:')}\n${commandsSection}` : ''}`.trimEnd();
};
