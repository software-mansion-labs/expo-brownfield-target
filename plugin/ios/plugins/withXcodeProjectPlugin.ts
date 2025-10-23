import path from 'node:path';

import { type ConfigPlugin, withXcodeProject } from 'expo/config-plugins';

import {
  configureBuildPhases,
  configureBuildSettings,
  createFileFromTemplate,
  createFileFromTemplateAs,
  createFramework,
  createGroup,
  inferProjectName,
  mkdir,
} from '../utils';
import type { PluginConfig } from '../types';

const withXcodeProjectPlugin: ConfigPlugin<PluginConfig> = (
  config,
  pluginConfig,
) => {
  return withXcodeProject(config, (config) => {
    const projectName =
      config.modRequest.projectName ??
      inferProjectName(config.modRequest.platformProjectRoot);
    const projectRoot = config.modRequest.projectRoot;
    const xcodeProject = config.modResults;

    // Create a target for the framework
    const target = createFramework(
      xcodeProject,
      pluginConfig.targetName,
      pluginConfig.bundleIdentifier,
    );

    // Create a directory for the framework files
    const groupPath = path.join(projectRoot, 'ios', pluginConfig.targetName);
    mkdir(groupPath);
    // Create the brownfield entrypoint based on the template
    createFileFromTemplate('ExpoApp.swift', groupPath);
    // Create and properly add a new group for the framework
    createGroup(xcodeProject, pluginConfig.targetName, groupPath, [
      'ExpoApp.swift',
    ]);

    // Create 'Info.plist' and '<target-name>.entitlements' based on the templates
    createFileFromTemplate('Info.plist', groupPath, {
      bundleIdentifier: pluginConfig.bundleIdentifier,
      targetName: pluginConfig.targetName,
    });
    createFileFromTemplateAs(
      'Target.entitlements',
      groupPath,
      pluginConfig.targetName + '.entitlements',
    );

    // Configure build phases:
    // - Reference Expo app target's RN bundle script
    // - Add custom script for patching ExpoModulesProvider
    // - Add 'ExpoApp.swift' to the compile sources phase
    configureBuildPhases(
      xcodeProject,
      target,
      pluginConfig.targetName,
      projectName,
      [`${pluginConfig.targetName}/ExpoApp.swift`],
    );
    // Add the required build settings
    configureBuildSettings(
      xcodeProject,
      pluginConfig.targetName,
      config.ios?.buildNumber || '1',
      pluginConfig.bundleIdentifier,
    );

    return config;
  });
};

export default withXcodeProjectPlugin;
