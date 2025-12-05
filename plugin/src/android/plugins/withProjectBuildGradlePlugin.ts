import { type ConfigPlugin, withProjectBuildGradle } from 'expo/config-plugins';

import type { PluginConfig, Publication } from '../types';
import {
  localDirectoryRepository,
  localMavenRepository,
  remotePrivateBasicRepository,
  remotePrivateTokenRepository,
  remotePublicRepository,
} from '../utils';

const EXPO_APPLY_STATEMENT = 'apply plugin: "expo-root-project"';
const PLUGIN_CLASSPATH = 'expo.modules:publish';
const PLUGIN_NAME = 'expo-brownfield-publish';

const withProjectBuildGradlePlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withProjectBuildGradle(config, (config) => {
    if (config.modResults.contents.includes(PLUGIN_CLASSPATH)) {
      return config;
    }

    let lines = config.modResults.contents.split('\n');
    lines = addPluginClasspathStatement(lines);
    lines = addApplyStatement(lines);
    lines = addPublicationConfiguration(
      lines,
      pluginConfig.publishing,
      pluginConfig.projectRoot,
    );
    config.modResults.contents = lines.join('\n');

    return config;
  });
};

const addPluginClasspathStatement = (lines: string[]): string[] => {
  const statement = `    classpath('${PLUGIN_CLASSPATH}')`;
  const lastClasspathIndex = lines.findLastIndex((line) =>
    line.includes('classpath('),
  );

  lines = [
    ...lines.slice(0, lastClasspathIndex + 1),
    statement,
    ...lines.slice(lastClasspathIndex + 1),
  ];

  return lines;
};

const addApplyStatement = (lines: string[]): string[] => {
  const statement = `apply plugin: "${PLUGIN_NAME}"`;
  const expoApplyIndex = lines.findIndex((line) =>
    line.includes(EXPO_APPLY_STATEMENT),
  );

  if (expoApplyIndex === -1) {
    throw new Error(
      'Error: "expo-root-project" apply statement not found in the project build.gradle file',
    );
  }

  lines = [
    ...lines.slice(0, expoApplyIndex + 1),
    statement,
    ...lines.slice(expoApplyIndex + 1),
  ];

  return lines;
};

const addPublicationConfiguration = (
  lines: string[],
  publications: Publication[],
  projectRoot: string,
): string[] => {
  lines = [
    ...lines,
    'expoBrownfieldPublishPlugin {',
    '    publications {',
    ...createPublicationConfigurations(publications, projectRoot),
    '    }',
    '}',
  ];

  return lines;
};

const createPublicationConfigurations = (
  publications: Publication[],
  projectRoot: string,
): string[] => {
  const configs: string[] = [];

  publications.forEach((publication) => {
    switch (publication.type) {
      // Local Maven publication
      case 'localMaven':
        configs.push(...localMavenRepository(configs));
        break;
      // Local Directory publication
      case 'localDirectory':
        configs.push(
          ...localDirectoryRepository(configs, projectRoot, publication),
        );
        break;
      // Remote Public publication (without authentication)
      case 'remotePublic':
        configs.push(...remotePublicRepository(configs, publication));
        break;
      // Remote Private publication (basic authentication)
      case 'remotePrivateBasic':
        configs.push(...remotePrivateBasicRepository(configs, publication));
        break;
      // Remote Private publication (token-based authentication)
      case 'remotePrivateToken':
        configs.push(...remotePrivateTokenRepository(configs, publication));
        break;
      default:
        break;
    }
  });

  return configs;
};

export default withProjectBuildGradlePlugin;
