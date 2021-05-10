<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("components", "config", "existsHWC")}

<#list components as comp >
  killall ${comp}
</#list>

find hwc -name "*.py" -exec pkill -f '{}' \;

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  docker stop dcpsinforepo
</#if>