<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "instanceName", "config")}
<#if config.getSplittingMode().toString() != "OFF">
    <#assign lineBreak = "\\">
<#else>
    <#assign lineBreak = ")">
</#if>
CONTAINER=$(docker run -d --rm \
<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
--net montithings \
<#else>
--net=host \<#--use host network for multicast support-->
</#if>
--cap-add=NET_ADMIN \<#-- allows simulating network delay-->
<#if config.getMessageBroker().toString() == "DDS">
  -v ${r"${PWD}"}/dcpsconfig.ini:/usr/src/app/build/bin/dcpsconfig.ini \
</#if>
--name ${instanceName} -h ${instanceName} ${typeName?lower_case}:latest --name ${instanceName} ${lineBreak}
<#if config.getMessageBroker().toString() == "OFF" && config.getSplittingMode().toString() != "OFF">
  --managementPort 30006 --dataPortArg 30007)
</#if>
<#if config.getMessageBroker().toString() == "MQTT">
  --brokerHostname ${r"${mqttip}"} --brokerPort 1883 --localHostname ${r"${localmqttip}"})
</#if>
<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  --DCPSInfoRepo dcpsinforepo:12345 --DCPSConfigFile dcpsconfig.ini)
</#if>
<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() != "DISTRIBUTED">
  --DCPSConfigFile dcpsconfig.ini)
</#if>
echo docker stop $CONTAINER >> "$SCRIPTPATH"/dockerStop.sh
echo docker kill $CONTAINER >> "$SCRIPTPATH"/dockerKill.sh