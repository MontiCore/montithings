#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

docker run --rm -d --net=montithings --name logtracer_middleware -p 3000:3000  monithings.logtracer_middleware --DCPSConfigFile dcpsconfig.ini
