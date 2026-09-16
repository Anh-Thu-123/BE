#!/bin/sh
# Minimal Maven launcher: uses a local Maven distribution under .mvn/dist,
# downloading it on first run. Avoids the official maven-wrapper bootstrap,
# which breaks when the project path contains non-ASCII/Unicode characters
# (observed on Windows with a Vietnamese-named directory).
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
MVN_VERSION=3.9.9
MVN_HOME="$DIR/.mvn/dist/apache-maven-$MVN_VERSION"

if [ ! -x "$MVN_HOME/bin/mvn" ]; then
  echo "Local Maven not found, downloading apache-maven-$MVN_VERSION ..." >&2
  mkdir -p "$DIR/.mvn/dist"
  TMP_ZIP="$DIR/.mvn/dist/maven.zip"
  curl -sSL -o "$TMP_ZIP" "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MVN_VERSION/apache-maven-$MVN_VERSION-bin.zip"
  (cd "$DIR/.mvn/dist" && unzip -q maven.zip && rm -f maven.zip)
  chmod +x "$MVN_HOME/bin/mvn"
fi

exec "$MVN_HOME/bin/mvn" "$@"
