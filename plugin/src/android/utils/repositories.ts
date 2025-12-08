import path from 'node:path';

import {
  LocalDirectoryPublication,
  Publication,
  RemotePrivateBasicPublication,
  RemotePublicPublication,
} from '../types';

const repositoryTemplates = {
  localMaven: () => [
    '    localDefault {',
    '        type = "localMaven"',
    '    }',
  ],
  localDirectory: (
    count: number,
    publication: LocalDirectoryPublication,
    projectRoot: string,
  ) => {
    const nameOrPlaceholder = publication.name ?? `localDirectory${count + 1}`;
    return [
      `    ${nameOrPlaceholder} {`,
      '        type = "localDirectory"',
      `        url = "file://${standardizePath(publication.path, projectRoot)}"`,
      '    }',
    ];
  },
  remotePublic: (
    count: number,
    publication: RemotePublicPublication,
    _projectRoot: string,
  ) => {
    const nameOrPlaceholder = publication.name ?? `remotePublic${count + 1}`;
    return [
      `    ${nameOrPlaceholder} {`,
      '        type = "remotePublic"',
      `        url = "${publication.url}"`,
      '    }',
    ];
  },
  remotePrivate: (
    count: number,
    publication: RemotePrivateBasicPublication,
    _projectRoot: string,
  ) => {
    const nameOrPlaceholder = publication.name ?? `remotePrivate${count + 1}`;
    return [
      `    ${nameOrPlaceholder} {`,
      '        type = "remotePrivate"',
      `        url = "${publication.url}"`,
      `        username = "${publication.username}"`,
      `        password = "${publication.password}"`,
      '    }',
    ];
  },
} as const;

export const addRepository = (
  lines: string[],
  projectRoot: string,
  publication: Publication,
) => {
  switch (publication.type) {
    case 'localMaven':
      const isAlreadyAdded = countOccurences(lines, 'localDefault') > 0;
      return isAlreadyAdded ? [] : repositoryTemplates.localMaven();
    case 'localDirectory':
    case 'remotePublic':
    case 'remotePrivate':
      const count = countOccurences(lines, `type = "${publication.type}"`);
      return repositoryTemplates[publication.type](
        count,
        // @ts-expect-error - TypeScript can't narrow union in fall-through case
        publication,
        projectRoot,
      );
    default:
      // @ts-expect-error - Non-existent, invalid publication type
      console.warn(`Unknown publication type: "${publication.type}"`);
      return [];
  }
};

const countOccurences = (lines: string[], pattern: string) => {
  return lines.filter((line) => line.includes(pattern)).length;
};

const standardizePath = (url: string, projectRoot: string) => {
  return path.isAbsolute(url) ? url : path.join(projectRoot, url);
};
