#!/bin/bash
set -e

LIBRARY_NAME=${LIBRARY_NAME:-"brownfield"}

cd app/android

if [ -d "$LIBRARY_NAME" ]; then
  echo "LIBRARY_NAME is not defined"
  echo "Searching for brownfield library name..."

  LIBRARY_NAME=$(basename $(dirname $(find . -name 'ReactNativeFragment.kt')))
  if [ -z "$LIBRARY_NAME" ]; then
    echo "Error: couldn't infer the brownfield library name"
    exit 1
  fi

  echo -e "Inferred the library name to be: $LIBRARY_NAME\n"
fi

echo "Building brownfield library..."

echo "Running ./gradlew clean..."
./gradlew :$LIBRARY_NAME:clean

# echo "Building debug AAR..."
# ./gradlew :brownfield:assembleDebug

echo "Building release AAR..."
./gradlew :$LIBRARY_NAME:assembleRelease

echo "Publishing AAR to local Maven repo..."
./gradlew :$LIBRARY_NAME:publishMavenAarPublicationToMavenLocal
