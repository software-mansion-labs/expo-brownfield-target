#!/bin/bash
set -e

LIBRARY_NAME=${LIBRARY_NAME:-"brownfield"}

cd app/android

if [ ! -d "$LIBRARY_NAME" ]; then
  echo "LIBRARY_NAME is not defined"
  echo "Searching for brownfield library name..."

  FOUND_PATH=$(find . -name 'ReactNativeFragment.kt' | head -1)
  if [ -z "$FOUND_PATH" ]; then
    echo "Error: couldn't infer the brownfield library name"
    exit 1
  fi

  LIBRARY_NAME=$(echo "$FOUND_PATH" | sed 's|^\./||' | cut -d/ -f1)
  if [ -z "$LIBRARY_NAME" ]; then
    echo "Error: couldn't extract brownfield library name from path: $FOUND_PATH"
    exit 1
  fi

  echo -e "Inferred the library name to be: $LIBRARY_NAME\n"
fi

echo "Building brownfield library..."

echo "Running ./gradlew clean..."
./gradlew :$LIBRARY_NAME:clean

# echo "Building debug AAR..."
# ./gradlew :$LIBRARY_NAME:assembleDebug

echo "Building release AAR..."
./gradlew :$LIBRARY_NAME:assembleRelease

echo "Publishing AAR to local Maven repo..."
./gradlew :$LIBRARY_NAME:publishMavenAarPublicationToMavenLocal
