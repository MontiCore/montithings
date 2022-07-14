#!/bin/bash

# (c) https://github.com/MontiCore/monticore

# config
JAR_NAME="cli-7.0.0-SNAPSHOT-cli.jar"
JAR_DOWNLOAD_URL="https://nexus.se.rwth-aachen.de/repository/montiarc-snapshots/montithings/cli/7.0.0-SNAPSHOT/cli-7.0.0-20220711.234119-712-cli.jar"
MT_SH_DOWNLOAD_URL="https://raw.githubusercontent.com/MontiCore/montithings/develop/cli/montithings"
MT_FULL_INSTALL_SKRIPT_URL="https://raw.githubusercontent.com/MontiCore/montithings/develop/installLinux.sh"

#############################################

read -r -p "Would you like to run the Montithings installer as well? (y/n): " answer
if answer="y"
then
  echo "Installing Montithings"
  export SKIP_MVN=1
  curl -sfL $MT_FULL_INSTALL_SKRIPT_URL | sh
fi

echo "Installing Montithings CLI"

mkdir -p $HOME/.montithings/jar/
curl -o $HOME/.montithings/jar/$JAR_NAME $JAR_DOWNLOAD_URL
sudo curl -o /usr/local/bin/montithings $MT_SH_DOWNLOAD_URL
sudo chmod -v a+x /usr/local/bin/montithings

echo "Montithings CLI installed successfully!"
montithings
