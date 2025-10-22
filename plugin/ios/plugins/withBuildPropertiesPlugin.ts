import withBuildProperties from "expo-build-properties";
import type { ConfigPlugin } from "expo/config-plugins";

const withBuildPropertiesPlugin: ConfigPlugin = (config) => {
  return withBuildProperties(config, { ios: { buildReactNativeFromSource: true } });
};

export default withBuildPropertiesPlugin;
