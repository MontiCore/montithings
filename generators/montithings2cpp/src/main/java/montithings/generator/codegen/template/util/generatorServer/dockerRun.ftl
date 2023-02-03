${tc.signature("config","what")}
<#include "/template/Preamble.ftl">

# (c) https://github.com/MontiCore/monticore

# Create a dedicated network named montithings. Skip if it is already present.
docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

rm -f dockerStop.sh
rm -f dockerKill.sh

touch dockerStop.sh
touch dockerKill.sh

# Unfortunately, the docker option --net=host is not supported by macOS and is ignored.
# Therefore, we have to find out what os is used and set the MQTT ip accordingly
# On Linux, localhost is used since the host network is used (this is done to allow multicast, see the DDS broker).
# On macOS, the montithings network is used instead. Therefore, we have to pass the ip of the host machine.
os="${r"$(uname -s)"}"
case "${r"${os}"}" in
    Linux*)
            mqttip=127.0.0.1
            localmqttip=127.0.0.1;;
    Darwin*)
            mqttip=host.docker.internal
            localmqttip=host.docker.internal;;
    *)          echo "unknown os!" && exit 1
esac


      





CONTAINER=$(docker run  -d --rm \
--net=host \
--cap-add=NET_ADMIN \
--name ${config.getMainComponent()?lower_case}.generator-server -h ${config.getMainComponent()?lower_case}.generator-server ${config.getMainComponent()?lower_case}.generator-server:latest ${r"${mqttip}"} 1883 ${r"${localmqttip}"})
echo docker stop $CONTAINER >> dockerStop.sh
echo docker kill $CONTAINER >> dockerKill.sh


chmod +x dockerStop.sh
chmod +x dockerKill.sh