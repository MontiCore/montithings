#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

docker run --rm --net=montithings --name recorder -v ${PWD}:/usr/src/app/services/recorder/recordings recorder --DCPSInfoRepo dcpsinforepo:12345 --DCPSConfigFile dcpsconfig.ini
