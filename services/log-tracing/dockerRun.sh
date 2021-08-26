#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

./frontend/dockerRun.sh
docker run --rm -d --net=montithings --name logtracer_middleware -p 8080:8080 registry.git.rwth-aachen.de/monticore/montithings/core/logtracer_middleware --DCPSConfigFile dcpsconfig.ini --mqtt-broker-host host.docker.internal "$@"
