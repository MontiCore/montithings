#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker run --rm --net=montithings --name recorder -v ${PWD}/docker_recordings:/usr/src/app/services/recorder/recordings monithings.recorder --DCPSInfoRepo dcpsinforepo:12345 --DCPSConfigFile dcpsconfig.ini
