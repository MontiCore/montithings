<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "instanceName", "config")}
<#if config.getSplittingMode().toString() != "OFF">
    <#assign lineBreak = "\\">
<#else>
    <#assign lineBreak = ")">
</#if>
CONTAINER=$(docker run -d --rm \
--net=host \<#--use host network for multicast support-->
--cap-add=NET_ADMIN \<#-- allows simulating network delay-->
--name ${instanceName} -h ${instanceName} ${typeName?lower_case}:latest --name ${instanceName} ${lineBreak}
--brokerPort 1883 --localHostname ${r"${localmqttip}"})
echo docker stop $CONTAINER >> "$SCRIPTPATH"/dockerStop.sh