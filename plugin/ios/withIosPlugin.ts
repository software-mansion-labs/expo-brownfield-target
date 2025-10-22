import type { ConfigPlugin } from "expo/config-plugins";
import {
  withBuildPropertiesPlugin,
  withPodfilePropertiesPlugin,
} from "./plugins";

const withIosPlugin: ConfigPlugin = (config) => {
  config = withPodfilePropertiesPlugin(config);
  config = withBuildPropertiesPlugin(config);
  return config;
};

export default withIosPlugin;
