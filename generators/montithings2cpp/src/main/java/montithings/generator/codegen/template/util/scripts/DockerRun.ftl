<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

<#if config.getSplittingMode().toString() != "OFF">
  <#assign lineBreak = "\\">
<#else>
  <#assign lineBreak = ")">
</#if>

rm -f dockerStop.sh
<#list instances as pair >
  CONTAINER=$(docker run -d --rm --net=host  -v ${r"${PWD}"}/dcpsconfig.ini:/usr/src/app/build/bin/dcpsconfig.ini  --name ${pair.getValue()} ${pair.getKey().fullName?lower_case}:latest ${pair.getValue()} ${lineBreak}
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
</#list>

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  # Start DCPSInfoRepo
  CONTAINER=$(docker run --name dcpsinforepo -d -p 12345:12345 registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo)
  echo docker stop $CONTAINER >> dockerStop.sh
</#if>
chmod +x dockerStop.sh