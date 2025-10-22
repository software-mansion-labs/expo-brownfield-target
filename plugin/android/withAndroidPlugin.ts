import type { ConfigPlugin } from "expo/config-plugins";

const withAndroidPlugin: ConfigPlugin = (config) => {
  console.log("Test: Android plugin");
  return config;
};

export default withAndroidPlugin;
