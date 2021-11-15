<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("components", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}

<#list components as comp >
  pkill -f ${comp}
</#list>
<#list sensorActuatorPorts as port >
  pkill -f ${port}
</#list>

if [ -d "hwc" ]; then
find hwc -name "*.py" -exec pkill -f '{}' \;
fi
  
<#if config.getMessageBroker().toString() == "MQTT">
<#if hwcPythonScripts?size!=0>
pkill -f python/sensoractuatormanager.py
</#if>
mosquitto_sub -h localhost -W 1 -F '%t' -t '#' | grep '^/sensorActuator/config' | while read i ; do  mosquitto_pub -h localhost -r -d -t "$i" -n; done > /dev/null 2>&1
</#if>

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  docker stop dcpsinforepo
</#if>