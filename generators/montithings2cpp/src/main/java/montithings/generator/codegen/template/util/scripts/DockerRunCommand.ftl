<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "instanceName", "config")}
<#include "/template/ConfigPreamble.ftl">

<#if !(splittingModeDisabled)>
    <#assign lineBreak = "\\">
<#else>
    <#assign lineBreak = ")">
</#if>
CONTAINER=$(docker run -d --rm \
<#if brokerIsDDS && splittingModeIsDistributed>
--net montithings \
<#else>
--net=host \<#--use host network for multicast support-->
</#if>
--cap-add=NET_ADMIN \<#-- allows simulating network delay-->
<#if brokerIsDDS>
  -v ${r"${PWD}"}/dcpsconfig.ini:/usr/src/app/build/bin/dcpsconfig.ini \
</#if>
--name ${instanceName} -h ${instanceName} ${typeName?lower_case}:latest --name ${instanceName} ${lineBreak}
<#if brokerDisabled && !(splittingModeDisabled)>
  --managementPort 30006 --dataPortArg 30007)
</#if>
<#if brokerIsMQTT>
  --brokerHostname ${r"${mqttip}"} --brokerPort 1883 --localHostname ${r"${localmqttip}"})
</#if>
<#if brokerIsDDS && splittingModeIsDistributed>
  --DCPSInfoRepo dcpsinforepo:12345 --DCPSConfigFile dcpsconfig.ini)
</#if>
<#if brokerIsDDS && !(splittingModeIsDistributed)>
  --DCPSConfigFile dcpsconfig.ini)
</#if>
echo docker stop $CONTAINER >> "$SCRIPTPATH"/dockerStop.sh
echo docker kill $CONTAINER >> "$SCRIPTPATH"/dockerKill.sh