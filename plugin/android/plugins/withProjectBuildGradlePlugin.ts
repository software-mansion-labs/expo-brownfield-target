import { type ConfigPlugin, withProjectBuildGradle } from 'expo/config-plugins';

const BROWNFIELD_PLUGIN = {
  name: 'com.callstack.react:brownfield-gradle-plugin',
  version: '0.5.0',
} as const;

const withProjectBuildGradlePlugin: ConfigPlugin = (config) => {
  const dependencyLine = `    classpath("${BROWNFIELD_PLUGIN.name}:${BROWNFIELD_PLUGIN.version}")`;

  return withProjectBuildGradle(config, (config) => {
    if (config.modResults.contents.includes(BROWNFIELD_PLUGIN.name)) {
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
