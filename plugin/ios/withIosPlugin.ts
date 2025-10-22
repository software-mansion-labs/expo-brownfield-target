import type { ConfigPlugin } from "expo/config-plugins";

const withIosPlugin: ConfigPlugin = (config) => {
  console.log("Test: iOS plugin");
  return config;
};

export default withIosPlugin;
