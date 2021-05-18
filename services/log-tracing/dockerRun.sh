#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker run --rm --net=montithings --name logtracer_middleware  monithings.logtracer_middleware --DCPSInfoRepo dcpsinforepo:12345 --DCPSConfigFile dcpsconfig.ini
