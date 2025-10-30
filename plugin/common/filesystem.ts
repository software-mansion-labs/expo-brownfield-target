import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import path from 'node:path';
import type { PlatformString } from './types';

export const mkdir = (path: string, recursive: boolean = false) => {
  mkdirSync(path, {
    recursive,
  });
};

const interpolateVariables = (
  str: string,
  variables: Record<string, unknown>,
): string => {
  const variableRegex = /\${{[A-z0-9]+}}/;
  let match = variableRegex.exec(str);
  while (match) {
    const variable = match[0].slice(3, -2);
    str = str.replace(match[0], String(variables[variable]));
    match = variableRegex.exec(str);
  }

  return str;
};

const readTemplate = (template: string, platform?: PlatformString): string => {
  const templatesPath = path.join(
    __filename,
    '../..',
    'templates',
    platform ?? '.',
  );
  const templatePath = path.join(templatesPath, template);

  if (!existsSync(templatePath)) {
    throw new Error(`Template ${template} doesn't exist at ${templatePath}`);
  }

  return readFileSync(templatePath).toString();
};

const createFileFromTemplateInternal = (
  template: string,
  at: string,
  dest: string,
  platform?: PlatformString,
  variables?: Record<string, unknown>,
) => {
  let templateContents = readTemplate(template, platform);
  if (variables) {
    templateContents = interpolateVariables(templateContents, variables);
  }

  const destPath = path.join(at, dest);
  writeFileSync(destPath, templateContents);
};

export const createFileFromTemplate = (
  template: string,
  at: string,
  platform?: PlatformString,
  variables?: Record<string, unknown>,
) => {
  createFileFromTemplateInternal(template, at, template, platform, variables);
};

export const createFileFromTemplateAs = (
  template: string,
  at: string,
  as: string,
  platform?: PlatformString,
  variables?: Record<string, unknown>,
) => {
  createFileFromTemplateInternal(template, at, as, platform, variables);
};

export const readFromTemplate = (
  template: string,
  variables?: Record<string, unknown>,
): string => {
  let templateContents = readTemplate(template);
  if (variables) {
    templateContents = interpolateVariables(templateContents, variables);
  }

  return templateContents;
};
