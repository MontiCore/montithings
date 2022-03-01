<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "instanceName", "config")}
<#include "/template/ConfigPreamble.ftl">

<#if !(splittingModeDisabled)>
    <#assign lineBreak = "\\">
<#else>
    <#assign lineBreak = ")">
</#if>
CONTAINER=$(docker run -d --rm \
--net=host \<#--use host network for multicast support-->
--cap-add=NET_ADMIN \<#-- allows simulating network delay-->
--name ${instanceName} -h ${instanceName} ${typeName?lower_case}:latest --name ${instanceName} ${lineBreak}
--brokerPort 1883 --localHostname ${r"${localmqttip}"})
echo docker stop $CONTAINER >> dockerStop.sh