#!/bin/sh
# (c) https://github.com/MontiCore/monticore

# Unfortunately, the docker option --net=host is not supported by iOS and is ignored.
# Therefore, we have to find out what os is used and set the MQTT ip accordingly
# On Linux, localhost is used since the host network is used (this is done to allow multicast, see the DDS broker).
# On iOS, the montithings network is used instead. Therefore, we have to pass the ip of the host machine.
os="$(uname -s)"
network=""
case "${os}" in
    Linux*)     network="--net=host";;
    Darwin*)    network="";;
    *)          echo "unknown os!" && exit 1
esac

docker run --rm -d ${network} -p 3000:3000 --name logtracer-frontend montithings/logtracer-frontend
