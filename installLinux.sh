#!/bin/bash
# (c) https://github.com/MontiCore/monticore

# Check if a command is available on this system
# Taken from https://get.docker.com/
command_exists() {
	command -v "$@" > /dev/null 2>&1
}

# Stop on first error
set -e

MONTITHINGS_DIRECTORY=$PWD

# Leave MontiThings Project for installing dependencies
[ -d dependencies ] || mkdir -p dependencies
cd dependencies

# Install packages
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install -y g++ git make cmake ninja-build mosquitto-dev libmosquitto-dev curl maven openjdk-11-jdk

# Install Docker 
if ! command_exists docker
then
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker
fi

# Check if we got a recent enough CMake version
CMAKE_VERSION=$(cmake --version | grep version | grep -Po '\d+.\d+.\d+')
if ( ! printf '%s\n%s\n' "3.13" "$CMAKE_VERSION" | sort --check=quiet --version-sort )
then
sudo apt install snapd
sudo snap install cmake --classic
if test -f "/usr/bin/cmake"; then
  sudo mv "/usr/bin/cmake" "/usr/bin/cmake-old"
  echo "/usr/bin/cmake will be updated, old version is moved to /usr/bin/cmake-old"
fi
sudo ln -s /snap/bin/cmake /usr/bin/cmake
fi

# Install NNG
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
ninja test
sudo ninja install
fi

cd $MONTITHINGS_DIRECTORY

# Install MontiThings
mvn clean install -Dexec.skip
