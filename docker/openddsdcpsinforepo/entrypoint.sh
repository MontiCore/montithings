#!/bin/sh
# (c) https://github.com/MontiCore/monticore
ip=$(hostname -i)
exec /usr/src/app/DCPSInfoRepo -ORBListenEndpoints iiop://"$ip":12345 "$@"