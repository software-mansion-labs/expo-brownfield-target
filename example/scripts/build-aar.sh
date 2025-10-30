#!/bin/bash
set -e

cd app/android

./gradlew :brownfield:assembleDebug
./gradlew :brownfield:assembleRelease
