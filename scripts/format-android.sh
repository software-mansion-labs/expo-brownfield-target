#!/bin/bash
set -e

DIRECTORIES=(
  "android/src"
  "example/android/app"
  "gradle-plugins"
)

for DIRECTORY in "${DIRECTORIES[@]}"; do
  find $DIRECTORY -name "*.kt" -o -name "*.kts" | xargs ktfmt --google-style
done
