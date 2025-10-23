export const Constants = {
  BuildPhase: {
    PatchExpoPhase: "Patch ExpoModulesProvider",
    RNBundlePhase: "Bundle React Native code and images",
    Script: "PBXShellScriptBuildPhase",
    Sources: "PBXSourcesBuildPhase",
  },
  Target: {
    ApplicationProductType: '"com.apple.product-type.application"',
    FallbackBundleIdentifier: "com.example.expo-brownfield",
    Framework: "framework",
    Name: "BrownfieldApp",
  },
  Utils: {
    XCEmptyString: '""', // Empty string needs to be double quoted
  },
} as const;
