#!/bin/bash

# (c) https://github.com/MontiCore/monticore

# config
JAR_NAME="cli-7.0.0-SNAPSHOT-cli.jar"
JAR_DOWNLOAD_URL="https://nexus.se.rwth-aachen.de/service/rest/v1/search/assets/download?sort=version&repository=public&maven.groupId=montithings&maven.artifactId=cli&maven.extension=jar&maven.classifier=cli"
MT_CLI_SH_DOWNLOAD_URL="https://raw.githubusercontent.com/MontiCore/montithings/develop/cli/montithings"
MT_FULL_INSTALL_SCRIPT_URL="https://raw.githubusercontent.com/MontiCore/montithings/develop/installLinux.sh"

#############################################

read -r -p "Would you like to install the MontiThings dependencies as well? (y/N): " answer
if [ "$answer" = "y" ]
then
  echo "Installing MontiThings"
  export SKIP_MVN=1
  curl -sfL $MT_FULL_INSTALL_SCRIPT_URL | sh
fi

echo "Installing MontiThings CLI"

mkdir -p $HOME/.montithings/jar/
curl -o $HOME/.montithings/jar/$JAR_NAME $JAR_DOWNLOAD_URL
sudo curl -o /usr/local/bin/montithings $MT_CLI_SH_DOWNLOAD_URL
sudo chmod -v a+x /usr/local/bin/montithings

echo "CLI installed successfully!"
echo '

  _____  ___                __  _   ___________    _
 /__   |/  /  ___________  / /_(_) / ___  __/ /_  (_)___  ____   __
   / /|_/ / / __ \__/ __ \/ __/ / (_)  / / / __ \/ / __ \/ __ `//_ \
  / /  / /_/ /_/ / / / / / /_/ /_   __/ / / / / / / / / / /_/ /___) )_
 /_/  /____\____/ /_/ /_/\__/___/  /___/ /_/ /_/_/_/ /_/\__, /(______/
                                                       /____/

'
montithings
