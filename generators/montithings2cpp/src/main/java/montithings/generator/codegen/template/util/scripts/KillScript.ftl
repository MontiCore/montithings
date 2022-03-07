<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("components", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">

<#list components as comp >
  pkill -f ${comp}
</#list>
<#list sensorActuatorPorts as port >
  pkill -f ${port}
</#list>

if [ -d "hwc" ]; then
find hwc -name "*.py" -exec pkill -f '{}' \;
fi
  
<#if brokerIsMQTT>
<#if hwcPythonScripts?size!=0>
pkill -f python/sensoractuatormanager.py
</#if>
mosquitto_sub -h localhost -W 1 -F '%t' -t '#' 2>/dev/null | grep '^/sensorActuator/config' | while read i ; do  mosquitto_pub -h localhost -r -t "$i" -n; done > /dev/null 2>&1
</#if>

<#if brokerIsDDS && splittingModeIsDistributed>
  docker stop dcpsinforepo
</#if>