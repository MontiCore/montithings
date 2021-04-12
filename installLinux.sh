#!/bin/bash
# (c) https://github.com/MontiCore/monticore 

# Stop on first error
set -e

MONTITHINGS_DIRECTORY=$PWD

# Check that settings.xml exists
if [ ! -f ~/.m2/settings.xml ]
then
echo "There is no settings.xml in your '~/.m2' Folder. Aborting."
exit 1
fi

# Leave MontiThings Project for installing dependencies
cd ..

# Install packages
sudo apt-get update
sudo apt-get install -y g++ git make cmake ninja-build mosquitto-dev libmosquitto-dev curl maven openjdk-11-jdk

# Install Docker 
if [ ! "$(command -v docker >/dev/null 2>&1)" ]
then
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker
fi

# Install NNG
git clone https://github.com/nanomsg/nng.git
cd nng
git checkout v1.3.0
mkdir build
cd build
cmake -G Ninja ..
ninja
ninja test
sudo ninja install
cd $MONTITHINGS_DIRECTORY

# Install MontiThings
mvn clean install -Dexec.skip