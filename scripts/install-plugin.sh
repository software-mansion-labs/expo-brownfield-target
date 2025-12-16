#!/bin/bash
set -e

APP_DIRECTORY="./example/app"

# Cleanup previous tarballs
rm -rf expo-brownfield-target-*.tgz
TARBALL_PATH=$(find $APP_DIRECTORY -name "expo-brownfield-target-*.tgz" | head -1)
if [ ! -z "$TARBALL_PATH" ]; then
  rm -rf $TARBALL_PATH
fi

# Build the plugin
npm pack

TARBALL_PATH=$(find . -name "expo-brownfield-target-*.tgz" | head -1)
if [ -z "$TARBALL_PATH" ]; then
  echo "Error: Unable to find the tarball"
  exit 1
fi

mv $TARBALL_PATH $APP_DIRECTORY

# Install the plugin
cd $APP_DIRECTORY
npm uninstall expo-brownfield-target
npm install $TARBALL_PATH

# Perform clean prebuild
# TODO: Hide behind a flag/variable
# rm -rf .expo android ios
# yes | npx expo prebuild --clean
