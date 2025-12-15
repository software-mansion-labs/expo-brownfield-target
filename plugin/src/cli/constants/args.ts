import arg from 'arg';

/**
 * General CLI arguments
 */
const generalArgs = {
  // Types
  '--help': arg.COUNT,
  '--version': arg.COUNT,
  // Aliases
  '-h': '--help',
  '-v': '--version',
} as const;

/**
 * Common build arguments shared by Android and iOS
 */
const buildCommonArgs = {
  // Types
  '--artifacts': String,
  '--debug': arg.COUNT,
  '--help': arg.COUNT,
  '--release': arg.COUNT,
  '--verbose': arg.COUNT,
  // Aliases
  '-a': '--artifacts',
  '-d': '--debug',
  '-h': '--help',
  '-r': '--release',
} as const;

/**
 * Android build arguments
 */
const buildAndroidArgs = {
  // Inherited
  ...buildCommonArgs,
  // Types
  '--library': String,
  '--no-publish': arg.COUNT,
  '--tasks': String,
  // Aliases
  '-l': '--library',
  '-t': '--tasks',
} as const;

/**
 * iOS build arguments
 */
const buildIosArgs = {
  // Inherited
  ...buildCommonArgs,
  // Types
  '--scheme': String,
  '--xcworkspace': String,
  // Aliases
  '-s': '--scheme',
  '-x': '--xcworkspace',
} as const;

/**
 * CLI arguments
 */
export const Args = {
  Android: buildAndroidArgs,
  General: generalArgs,
  IOS: buildIosArgs,
} as const;
