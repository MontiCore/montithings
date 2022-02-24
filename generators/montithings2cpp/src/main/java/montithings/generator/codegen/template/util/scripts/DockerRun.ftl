<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>
<#assign sensoractuatormanagerimage = "sensoractuatormanager">



rm -f dockerStop.sh
rm -f dockerKill.sh

# Create a dedicated network named montithings. Skip if it is already present.
docker network ls | grep montithings > /dev/null || docker network create --driver bridge montithings

# Unfortunately, the docker option --net=host is not supported by macOS and is ignored.
# Therefore, we have to find out what os is used and set the MQTT ip accordingly
# On Linux, localhost is used since the host network is used (this is done to allow multicast, see the DDS broker).
# On macOS, the montithings network is used instead. Therefore, we have to pass the ip of the host machine.
os="$(uname -s)"
case "${r"${os}"}" in
    Linux*)
            mqttip=127.0.0.1
            localmqttip=127.0.0.1;;
    Darwin*)
            mqttip=host.docker.internal
            localmqttip=host.docker.internal;;
    *)          echo "unknown os!" && exit 1
esac

<#if brokerIsDDS && splittingModeIsDistributed>
  # Start DCPSInfoRepo
  CONTAINER=$(docker run --name dcpsinforepo -h dcpsinforepo --rm -d --net montithings registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo)
  echo docker stop $CONTAINER >> dockerStop.sh
  echo docker kill $CONTAINER >> dockerKill.sh
</#if>

<#if splittingModeDisabled>
    ${tc.includeArgs("template.util.scripts.DockerRunCommand", [comp.getFullName(), comp.getFullName()?lower_case, config])}
<#else>
  <#list instances as pair >
      ${tc.includeArgs("template.util.scripts.DockerRunCommand", [pair.getKey().fullName, pair.getValue(), config])}
  </#list>
</#if>
<#if brokerIsMQTT>
    <#list sensorActuatorPorts as port >
        ${tc.includeArgs("template.util.scripts.DockerRunCommandSensorActuatorPorts", [port, port?lower_case, config])}
    </#list>
    <#list hwcPythonScripts as script >
        ${tc.includeArgs("template.util.scripts.DockerRunCommandPython", [script?lower_case, config])}
    </#list>
    ${tc.includeArgs("template.util.scripts.DockerRunCommandPython", [sensoractuatormanagerimage, config])}
</#if>

chmod +x dockerStop.sh
chmod +x dockerKill.sh
