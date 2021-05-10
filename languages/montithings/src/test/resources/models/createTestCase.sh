#!/bin/sh
# (c) https://github.com/MontiCore/monticore

set -e # Stop on first error

DIRECTORY=$(echo "$1" | tr '.' '/')

mkdir -p "$DIRECTORY"
cp -r ../template/* "$DIRECTORY"

MODELS=$(find "$DIRECTORY" -type f -name *.mt)
for MODEL in $MODELS; do
  # Add package name as second line of the file
  ex -sc "2s/^/package $1;\r/|x" "$MODEL"
done

