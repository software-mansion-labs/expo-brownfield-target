import { withPodfile, type ConfigPlugin } from "expo/config-plugins";
import {
  addCustomRubyScriptImport,
  addNewPodsTarget,
  Constants,
} from "../utils";

const withPodfilePlugin: ConfigPlugin = (config) => {
  return withPodfile(config, (config) => {
    config.modResults.contents = addCustomRubyScriptImport(
      config.modResults.contents
    );
    config.modResults.contents = addNewPodsTarget(
      config.modResults.contents,
      Constants.Target.Name
    );
    return config;
  });
};

export default withPodfilePlugin;
