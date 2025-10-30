#!/bin/bash
set -e

ARTIFACTS_DIR="artifacts"
SCHEME=""
XCODE_WORKSPACE=""
XCFRAMEWORK_NAME=""
CONFIGURATION=${CONFIGURATION:-"Release"}
VERBOSE=${VERBOSE:-"false"}
HERMES_XCFRAMEWORK="Pods/hermes-engine/destroot/Library/Frameworks/universal/hermes.xcframework"
APP_PROJECT_DIR="app/ios"

if [ -d "$ARTIFACTS_DIR" ]; then
  echo "Cleaning up previous brownfield artifacts..."
  rm -rf "$ARTIFACTS_DIR"
fi

if [ -z "$XCODE_WORKSPACE" ]; then
  echo "XCODE_WORKSPACE is not defined"
  echo "Searching for .xcworkspace in ios/..."

  XCODE_WORKSPACE=$(find "$APP_PROJECT_DIR" -name '*.xcworkspace' | head -1)
  if [ -z "$XCODE_WORKSPACE" ]; then
    echo "Error: couldn't infer the .xcworkspace path"
    exit 1
  fi

  echo -e "Found .xcworkspace: $XCODE_WORKSPACE\n"
fi

if [ -z "$SCHEME" ]; then
  echo "SCHEME is not defined"
  echo "Searching for brownfield target scheme..."

  SCHEME=$(basename $(dirname $(find "$APP_PROJECT_DIR" -name 'ExpoApp.swift')))
  if [ -z "$SCHEME" ]; then
    echo "Error: couldn't infer the brownfield scheme name"
    exit 1
  fi

  echo -e "Inferred the scheme name to be: $SCHEME\n"
fi

echo "Building scheme $SCHEME with conifguration $CONFIGURATION..."

if [ "$VERBOSE" == "true" ]; then
  xcodebuild \
    -workspace "$XCODE_WORKSPACE" \
    -scheme "$SCHEME" \
    -derivedDataPath "$APP_PROJECT_DIR/build" \
    -destination "generic/platform=iphoneos" \
    -destination "generic/platform=iphonesimulator" \
    -configuration "$CONFIGURATION"
else
  xcodebuild \
    -workspace "$XCODE_WORKSPACE" \
    -scheme "$SCHEME" \
    -derivedDataPath "$APP_PROJECT_DIR/build" \
    -destination "generic/platform=iphoneos" \
    -destination "generic/platform=iphonesimulator" \
    -configuration "$CONFIGURATION" > /dev/null 2>&1
fi

echo "Packaging all $SCHEME.framework into an .xcframework..."

XCF_NAME={XCFRAMEWORK_NAME:-"$SCHEME.xcframework"}
mkdir "$ARTIFACTS_DIR"

if [ "$VERBOSE" == "true" ]; then
  xcodebuild \
    -create-xcframework \
    -framework "./$APP_PROJECT_DIR/build/Build/Products/Release-iphoneos/$SCHEME.framework" \
    -framework "./$APP_PROJECT_DIR/build/Build/Products/Release-iphonesimulator/$SCHEME.framework" \
    -output "$ARTIFACTS_DIR/$SCHEME.xcframework"
else
  xcodebuild \
    -create-xcframework \
    -framework "./$APP_PROJECT_DIR/build/Build/Products/Release-iphoneos/$SCHEME.framework" \
    -framework "./$APP_PROJECT_DIR/build/Build/Products/Release-iphonesimulator/$SCHEME.framework" \
    -output "$ARTIFACTS_DIR/$SCHEME.xcframework" > /dev/null 2>&1
fi

echo -e "Created XCFramework: $ARTIFACTS_DIR/$SCHEME.xcframework\n"

echo -e "Copying hermes XCFramework from $HERMES_XCFRAMEWORK..."

if [ ! -d "$APP_PROJECT_DIR/$HERMES_XCFRAMEWORK" ]; then
  echo "Error: couldn't find hermes.xcframework at $HERMES_XCFRAMEWORK"
  exit 1
fi

cp -r "$APP_PROJECT_DIR/$HERMES_XCFRAMEWORK" "$ARTIFACTS_DIR"

echo "Build succeeded"
