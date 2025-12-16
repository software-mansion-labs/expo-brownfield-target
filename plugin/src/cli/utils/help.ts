import chalk from 'chalk';
import type { HelpMessageParams, HelpMessageSectionParams } from './types';

// TODO: Extract to constants file?
// Number of spaces to use for spacing between command/option and description
const SPACING = 30;

export const helpMessage = ({
  commands,
  options,
  promptCommand = '<command>',
  promptOptions = '<options>',
}: HelpMessageParams): string => {
  const optionsSection = helpMessageSection({
    items: options,
    left: ({ option, short }) => `${option}${short ? `, ${short}` : ''}`,
    right: ({ description }) => description,
    title: 'Options:',
  });

  const commandsSection = helpMessageSection({
    items: commands,
    left: ({ command, hasOptions }) =>
      `${command}${hasOptions ? ` [${promptOptions}]` : ''}`,
    right: ({ description }) => description,
    title: 'Commands:',
  });

  const usageSection = `${chalk.bold('Usage:')} expo-brownfield-target ${promptCommand}  [${promptOptions}]`;

  // TODO: Add new line at the end (check e.g. in Ghostty)?
  return `\n${usageSection}${optionsSection}${commandsSection}`;
};

export const helpMessageSection = <T>({
  items,
  left,
  right,
  title,
}: HelpMessageSectionParams<T>): string => {
  if (!items) {
    return '';
  }

  const content = items.reduce<string>((acc, item) => {
    const ls = left(item);
    const rs = right(item);
    const spacing = ' '.repeat(SPACING - ls.length);
    return `${acc}\n  ${ls}${spacing}${rs}`;
  }, '');

  return `\n\n${chalk.bold(title)}${content}`;
};
