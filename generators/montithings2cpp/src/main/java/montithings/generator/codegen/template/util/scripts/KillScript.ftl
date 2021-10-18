<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("components", "sensorActuatorPorts", "config", "existsHWC")}

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
pkill -f python/sensoractuatormanager.py

mosquitto_sub -h localhost -W 1 -F '%t' -t '#' | grep '^/sensorActuator/config' | while read i ; do  mosquitto_pub -h localhost -r -d -t "$i" -n; done
</#if>

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  docker stop dcpsinforepo
</#if>