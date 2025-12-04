import { type ConfigPlugin, withProjectBuildGradle } from 'expo/config-plugins';

const EXPO_APPLY_STATEMENT = 'apply plugin: "expo-root-project"';
const PLUGIN_CLASSPATH = 'expo.modules:publish';
const PLUGIN_NAME = 'expo-brownfield-publish';

const withProjectBuildGradlePlugin: ConfigPlugin = (config) => {
  return withProjectBuildGradle(config, (config) => {
    if (config.modResults.contents.includes(PLUGIN_CLASSPATH)) {
      return config;
    }

    let lines = config.modResults.contents.split('\n');
    lines = addPluginClasspathStatement(lines);
    lines = addApplyStatement(lines);
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
    throw new Error('Error: "expo-root-project" apply statement not found in the project build.gradle file');
  }

  lines = [
    ...lines.slice(0, expoApplyIndex + 1),
    statement,
    ...lines.slice(expoApplyIndex + 1),
  ];

  return lines;
};

export default withProjectBuildGradlePlugin;
