#!/bin/bash
# (c) https://github.com/MontiCore/monticore

# Check if a command is available on this system
# Taken from https://get.docker.com/
command_exists() {
  command -v "$@" > /dev/null 2>&1
}

# Check ARM
# if [ "$(uname -m)" = "arm64" ]
# Check Intel
# if [ "$(uname -m)" = "x86_64" ]


# Stop on first error
set -e

MONTITHINGS_DIRECTORY=$PWD

# Leave MontiThings Project for installing dependencies
[ -d dependencies ] || mkdir -p dependencies
cd dependencies

# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Initialize environment vars of SDKMAN
source ~/.sdkman/bin/sdkman-init.sh

# Install Java, Maven, Gradle
sdk install java 8.0.312-zulu
sdk install maven
sdk install gradle 6.9.1

# Install Homebrew
if ! command_exists brew
then
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
export PATH=$PATH:/opt/homebrew/bin
fi

# Install Dependencies
brew install cmake ninja mosquitto terraform azure-cli conan python
brew services start mosquitto
if ! command_exists docker
then
brew install --cask docker
fi

# Install NNG
if [ ! -d nng ]
then
git clone https://github.com/nanomsg/nng.git
cd nng
git checkout v1.3.0
mkdir build
cd build
cmake -G Ninja ..
ninja
ninja test || true # allowed to fail to enable GitPod builds
sudo ninja install
fi

cd $MONTITHINGS_DIRECTORY
rm -rf dependencies

# Install MontiThings
mvn clean install -Dexec.skip


