#!/bin/bash
# (c) https://github.com/MontiCore/monticore

#
# Set "export SKIP_MVN=1" to skip the maven build at the end of this script
# Or call "SKIP_MVN=1 ./installLinux.sh"
#

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
sudo apt-get install -y g++ git make cmake ninja-build mosquitto-dev libmosquitto-dev curl maven openjdk-11-jdk python3 python3-pip mosquitto-clients

pip3 install paho-mqtt

# Install Docker 
if ! command_exists docker
then
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker << SHELL
SHELL
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

if [ -z "${SKIP_MVN}" ] || [ "${SKIP_MVN}" != "1" ]
then
# Install MontiThings
mvn clean install -Dexec.skip
else
  echo "###################################"
  echo "MontiThings installed successfully!"
  echo "###################################"
fi
