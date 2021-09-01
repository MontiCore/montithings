#!/bin/sh
# (c) https://github.com/MontiCore/monticore

./frontend/dockerRunLocal.sh

# Unfortunately, the docker option --net=host is not supported by iOS and is ignored.
# Therefore, we have to find out what os is used and set the MQTT ip accordingly
# On Linux, localhost is used since the host network is used (this is done to allow multicast, see the DDS broker).
# On iOS, the montithings network is used instead. Therefore, we have to pass the ip of the host machine.
os="$(uname -s)"
network=""
case "${os}" in
    Linux*)     mqttip=127.0.0.1 && network="--net=host";;
    Darwin*)    mqttip=host.docker.internal;;
    *)          echo "unknown os!" && exit 1
esac

docker run --rm ${network} -p 8080:8080 --name logtracer_middleware  registry.git.rwth-aachen.de/monticore/montithings/core/logtracer_middleware --DCPSConfigFile dcpsconfig.ini --mqtt-broker-host ${mqttip} "$@"
