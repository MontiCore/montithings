<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scriptname", "config")}

CONTAINER=$(docker run -d --rm \
--net=host \<#--use host network for multicast support-->
--cap-add=NET_ADMIN \<#-- allows simulating network delay-->
--name ${scriptname} -h ${scriptname} ${scriptname}:latest --name ${scriptname} \
--brokerHostname ${"$"}{localmqttip} --brokerPort 1883)
echo docker stop $CONTAINER >> "$SCRIPTPATH"/dockerStop.sh