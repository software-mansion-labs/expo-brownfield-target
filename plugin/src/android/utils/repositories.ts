import path from 'node:path';

import { LocalDirectoryPublication, RemotePrivateBasicPublication, RemotePrivateTokenPublication, RemotePublicPublication } from '../types';

// TODO: Fix formatting, 2 spaces less
// TODO: Deduplicate into object?
export const localMavenRepository = (lines: string[]) => {
  const hasLocalMaven = lines.find((line) => line.includes('localDefault'));
  if (hasLocalMaven) {
    return [];
  }

  return [
    '        localDefault {',
    '            type = "localMaven"',
    '        }',
  ];
};

export const localDirectoryRepository = (
  lines: string[],
  projectRoot: string,
  publication: LocalDirectoryPublication,
) => {
  const url = path.isAbsolute(publication.path)
    ? publication.path
    : path.join(projectRoot, publication.path);

  if (publication.name) {
    return [
      `        ${publication.name} {`,
      '            type = "localDirectory"',
      `            url = "file://${url}"`,
      '        }',
    ];
  }

  const count = lines.filter(
    (line) => line.includes('localDirectory') && line.includes('{'),
  ).length;

  return [
    `        localDirectory${count + 1} {`,
    '            type = "localDirectory"',
    `            url = "file://${url}"`,
    '        }',
  ];
};

export const remotePublicRepository = (
  lines: string[],
  publication: RemotePublicPublication,
) => {
  if (publication.name) {
    return [
      `        ${publication.name} {`,
      '            type = "remotePublic"',
      `            url = "${publication.url}"`,
      '        }',
    ];
  }

  const count = lines.filter(
    (line) => line.includes('remotePublic') && line.includes('{'),
  ).length;

  return [
    `        remotePublic${count + 1} {`,
    '            type = "remotePublic"',
    `            url = "${publication.url}"`,
  ];
};

export const remotePrivateBasicRepository = (
  lines: string[],
  publication: RemotePrivateBasicPublication,
) => {
  if (publication.name) {
    return [
      `        ${publication.name} {`,
      '            type = "remotePrivateBasic"',
      `            url = "${publication.url}"`,
      `            username = "${publication.username}"`,
      `            password = "${publication.password}"`,
      '        }',
    ];
  }

  const count = lines.filter(
    (line) => line.includes('remotePrivateBasic') && line.includes('{'),
  ).length;

  return [
    `        remotePrivateBasic${count + 1} {`,
    '            type = "remotePrivateBasic"',
    `            url = "${publication.url}"`,
    `            username = "${publication.username}"`,
    `            password = "${publication.password}"`,
    '        }',
  ];
};

export const remotePrivateTokenRepository = (
  lines: string[],
  publication: RemotePrivateTokenPublication,
) => {
  if (publication.name) {
    return [
      `        ${publication.name} {`,
      '            type = "remotePrivateToken"',
      `            url = "${publication.url}"`,
      `            token = "${publication.token}"`,
      '        }',
    ];
  }

  const count = lines.filter(
    (line) => line.includes('remotePrivateToken') && line.includes('{'),
  ).length;

  return [
    `        remotePrivateToken${count + 1} {`,
    '            type = "remotePrivateToken"',
    `            url = "${publication.url}"`,
    `            token = "${publication.token}"`,
    '        }',
  ];
};
