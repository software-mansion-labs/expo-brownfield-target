import { mkdirSync } from 'node:fs';

export const mkdir = (path: string, recursive: boolean = false) => {
  mkdirSync(path, {
    recursive,
  });
};
