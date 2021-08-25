#!/bin/sh
# (c) https://github.com/MontiCore/monticore

./frontend/dockerRunLocal.sh
docker run --rm -d --net=host -p 8080:8080 --name logtracer_middleware  registry.git.rwth-aachen.de/monticore/montithings/core/logtracer_middleware --DCPSConfigFile dcpsconfig.ini "$@"
