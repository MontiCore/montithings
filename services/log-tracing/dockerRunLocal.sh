#!/bin/sh
# (c) https://github.com/MontiCore/monticore

./frontend/dockerRunLocal.sh
docker run --rm -d --net=host --name logtracer_middleware  monithings.logtracer_middleware --DCPSConfigFile dcpsconfig.ini
