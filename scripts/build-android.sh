#!/bin/bash
set -e

ANDROID_PROJECT_DIR="example/android"
DEBUG=${DEBUG:-"false"}
PACKAGE_NAME="com.pmleczek.android"

cd $ANDROID_PROJECT_DIR

./gradlew clean

if [ "$DEBUG" == "true" ]; then
  ./gradlew installDebug --refresh-dependencies 
else
  ./gradlew installRelease --refresh-dependencies 
fi

adb shell monkey -p $PACKAGE_NAME 1
