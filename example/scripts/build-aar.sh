#!/bin/bash
set -e

cd app/android

echo "Building brownfield library..."

echo "Building debug AAR..."
./gradlew :brownfield:assembleDebug

echo "Building release AAR..."
./gradlew :brownfield:assembleRelease

echo "Publishing AAR to local Maven repo..."
./gradlew :brownfield:publishMavenAarPublicationToMavenLocal
