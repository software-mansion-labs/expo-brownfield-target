import fs, { writeFileSync } from "node:fs";
import path from "node:path";

export const mkdir = (path: string, recursive: boolean = false) => {
  fs.mkdirSync(path, {
    recursive,
  });
};

const interpolateVariables = (
  str: string,
  variables: Record<string, unknown>
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

const readTemplate = (
  template: string,
): string => {
  const templatesPath = path.join(__filename, "../..", "templates");
  const templatePath = path.join(templatesPath, template);

  if (!fs.existsSync(templatePath)) {
    throw new Error();
  }

  return fs.readFileSync(templatePath).toString();
};

const createFileFromTemplateInternal = (
  template: string,
  at: string,
  dest: string,
  variables?: Record<string, unknown>
) => {
  let templateContents = readTemplate(template);
  if (variables) {
    templateContents = interpolateVariables(templateContents, variables);
  }

  const destPath = path.join(at, dest);
  writeFileSync(destPath, templateContents);
};

export const createFileFromTemplate = (
  template: string,
  at: string,
  variables?: Record<string, unknown>
) => {
  createFileFromTemplateInternal(template, at, template, variables);
};

export const createFileFromTemplateAs = (
  template: string,
  at: string,
  as: string,
  variables?: Record<string, unknown>
) => {
  createFileFromTemplateInternal(template, at, as, variables);
};

export const readFromTemplate = (
  template: string,
  variables?: Record<string, unknown>
): string => {
  let templateContents = readTemplate(template);
  if (variables) {
    templateContents = interpolateVariables(templateContents, variables);
  }

  return templateContents;
};
