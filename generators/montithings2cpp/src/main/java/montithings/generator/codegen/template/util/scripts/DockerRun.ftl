<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>
<#assign sensoractuatormanagerimage = "sensoractuatormanager">



rm -f dockerStop.sh

docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  # Start DCPSInfoRepo
  CONTAINER=$(docker run --name dcpsinforepo -h dcpsinforepo --rm -d --net montithings registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo)
  echo docker stop $CONTAINER >> dockerStop.sh
</#if>

<#if config.getSplittingMode().toString() == "OFF">
    ${tc.includeArgs("template.util.scripts.DockerRunCommand", [comp.getFullName(), comp.getFullName()?lower_case, config])}
<#else>
  <#list instances as pair >
      ${tc.includeArgs("template.util.scripts.DockerRunCommand", [pair.getKey().fullName, pair.getValue(), config])}
  </#list>
</#if>
<#if config.getMessageBroker().toString() == "MQTT">
    <#list sensorActuatorPorts as port >
        ${tc.includeArgs("template.util.scripts.DockerRunCommand", [port, port?lower_case, config])}
    </#list>
    <#list hwcPythonScripts as script >
        ${tc.includeArgs("template.util.scripts.DockerRunCommandPython", [script?lower_case, config])}
    </#list>
    ${tc.includeArgs("template.util.scripts.DockerRunCommandPython", [sensoractuatormanagerimage, config])}
</#if>

chmod +x dockerStop.sh