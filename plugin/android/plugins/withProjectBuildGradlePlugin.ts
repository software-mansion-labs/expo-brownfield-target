import { type ConfigPlugin, withProjectBuildGradle } from 'expo/config-plugins';

const withProjectBuildGradlePlugin: ConfigPlugin = (config) => {
  // TODO: Move to constants
  const brownfieldPluginName = 'com.callstack.react:brownfield-gradle-plugin';
  const brownfieldPluginVersion = '0.5.0';
  const dependencyLine = `    classpath("${brownfieldPluginName}:${brownfieldPluginVersion}")`;

  return withProjectBuildGradle(config, (config) => {
    if (config.modResults.contents.includes(brownfieldPluginName)) {
      return config;
    }

    let lines = config.modResults.contents.split('\n');
    const lastDepdendencyIndex = lines.findLastIndex((line) =>
      line.includes('classpath('),
    );

    lines = [
      ...lines.slice(0, lastDepdendencyIndex + 1),
      dependencyLine,
      ...lines.slice(lastDepdendencyIndex + 1),
    ];
    config.modResults.contents = lines.join('\n');

    return config;
  });
};

export default withProjectBuildGradlePlugin;
