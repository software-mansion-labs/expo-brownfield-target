import path from 'node:path';

import { type ConfigPlugin, withXcodeProject } from 'expo/config-plugins';

import type { PluginConfig } from '../types';
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
    // Create the SwiftUI brownfield entrypoint based on the template
    createFileFromTemplate('ReactNativeView.swift', groupPath);
    // Add the brownfield host manager initializer based on the template
    createFileFromTemplate('ReactNativeLoader.m', groupPath);
    // Create and properly add a new group for the framework
    createGroup(xcodeProject, pluginConfig.targetName, groupPath, [
      'ExpoApp.swift',
      'ReactNativeView.swift',
      'ReactNativeLoader.m',
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
    // - Add 'ExpoApp.swift', 'ReactNativeView.swift' and 'ReactNativeLoader.m'
    // to the compile sources phase
    configureBuildPhases(
      xcodeProject,
      target,
      pluginConfig.targetName,
      projectName,
      [
        `${pluginConfig.targetName}/ExpoApp.swift`,
        `${pluginConfig.targetName}/ReactNativeView.swift`,
        `${pluginConfig.targetName}/ReactNativeLoader.m`,
      ],
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
