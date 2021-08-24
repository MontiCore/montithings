#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

docker run --rm -d --net=montithings --name logtracer_frontend -p 3000:3000 registry.git.rwth-aachen.de/monticore/montithings/core/logtracer_frontend --DCPSConfigFile dcpsconfig.ini
