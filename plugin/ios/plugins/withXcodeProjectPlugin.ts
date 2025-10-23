import { withXcodeProject, type ConfigPlugin } from "expo/config-plugins";
import path from "node:path";
import {
  configureBuildPhases,
  configureBuildSettings,
  Constants,
  createFileFromTemplate,
  createFileFromTemplateAs,
  createFramework,
  createGroup,
  mkdir,
} from "../utils";

const withXcodeProjectPlugin: ConfigPlugin = (config) => {
  return withXcodeProject(config, (config) => {
    const projectRoot = config.modRequest.projectRoot;
    const xcodeProject = config.modResults;

    // Create the bundle identifier for the framework
    // Either based on value from Expo config
    // Or the default fallback ('com.example...')
    const bundleIdentifier = `${
      config.ios?.bundleIdentifier ?? Constants.Target.FallbackBundleIdentifier
    }.${Constants.Target.Name}`;

    // Create a target for the framework
    const target = createFramework(
      xcodeProject,
      Constants.Target.Name,
      bundleIdentifier
    );

    // Create a directory 'BrownfieldApp' for the framework files
    const groupPath = path.join(projectRoot, "ios", Constants.Target.Name);
    mkdir(groupPath);
    // Create the brownfield entrypoint based on the template
    createFileFromTemplate("ExpoApp.swift", groupPath);
    // Create and properly add a new group for the framework
    createGroup(xcodeProject, Constants.Target.Name, groupPath, [
      "ExpoApp.swift",
    ]);

    // Create 'Info.plist' and '<target-name>.entitlements' based on the templates
    createFileFromTemplate("Info.plist", groupPath, {
      targetName: Constants.Target.Name,
    });
    createFileFromTemplateAs(
      "Target.entitlements",
      groupPath,
      Constants.Target.Name + ".entitlements"
    );

    // Configure build phases:
    // - Reference Expo app target's RN bundle script
    // - Add custom script for patching ExpoModulesProvider
    // - Add 'ExpoApp.swift' to the compile sources phase
    configureBuildPhases(xcodeProject, target, [
      `${Constants.Target.Name}/ExpoApp.swift`,
    ]);
    // Add the required build settings
    configureBuildSettings(
      xcodeProject,
      Constants.Target.Name,
      config.ios?.buildNumber || "1",
      bundleIdentifier
    );

    return config;
  });
};

export default withXcodeProjectPlugin;
