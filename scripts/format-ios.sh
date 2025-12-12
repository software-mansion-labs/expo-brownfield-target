#!/bin/bash
set -e

DIRECTORIES=(
  "example/ios"
  "example/ios-swiftui"
  "ios"
)

for DIRECTORY in "${DIRECTORIES[@]}"; do
  find $DIRECTORY -name "*.swift" | xargs swiftformat
done
