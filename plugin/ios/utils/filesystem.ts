import fs from 'node:fs';
import {
  createFileFromTemplate as createFileFromTemplateCommon,
  createFileFromTemplateAs as createFileFromTemplateAsCommon,
} from '../../common/filesystem';

export const mkdir = (path: string, recursive: boolean = false) => {
  fs.mkdirSync(path, {
    recursive,
  });
};

export const createFileFromTemplate = (
  template: string,
  at: string,
  variables?: Record<string, unknown>,
) => {
  createFileFromTemplateCommon(template, at, 'ios', variables);
};

export const createFileFromTemplateAs = (
  template: string,
  at: string,
  as: string,
  variables?: Record<string, unknown>,
) => {
  createFileFromTemplateAsCommon(template, at, as, 'ios', variables);
};
