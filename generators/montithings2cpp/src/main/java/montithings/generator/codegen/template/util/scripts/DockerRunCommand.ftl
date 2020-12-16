<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "instanceName", "config")}
<#if config.getSplittingMode().toString() != "OFF">
    <#assign lineBreak = "\\">
<#else>
    <#assign lineBreak = ")">
</#if>
CONTAINER=$(docker run -d --rm --net=host \
<#if config.getMessageBroker().toString() == "DDS">
  -v ${r"${PWD}"}/dcpsconfig.ini:/usr/src/app/build/bin/dcpsconfig.ini \
</#if>
--name ${instanceName} ${typeName?lower_case}:latest ${instanceName} ${lineBreak}
<#if config.getMessageBroker().toString() == "OFF" && config.getSplittingMode().toString() != "OFF">
  30006 30007)
</#if>
<#if config.getMessageBroker().toString() == "MQTT">
  host.docker.internal 1883)
</#if>
<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  -DCPSInfoRepo localhost:12345 -DCPSConfigFile dcpsconfig.ini)
</#if>
<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() != "DISTRIBUTED">
  -DCPSConfigFile dcpsconfig.ini)
</#if>
echo docker stop $CONTAINER >> dockerStop.sh