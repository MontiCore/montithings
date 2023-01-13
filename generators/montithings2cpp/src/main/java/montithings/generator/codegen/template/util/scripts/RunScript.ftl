<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

set -e

<#if brokerIsDDS && splittingModeIsDistributed>
echo "Starting DCPSInfoRepo..."
docker run --name dcpsinforepo --rm -d -p 12345:12345 registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo
echo "Waiting 5 seconds..."
sleep 5
echo "Starting components..."
</#if>

<#list instances as pair >
  <#if brokerIsMQTT && ComponentHelper.hasHandwrittenPythonBehaviour(config.hwcPath, pair.key)>
    <#assign hwcPythonFile = ComponentHelper.getPythonMainScriptName(pair.key)>
    OLD_PYTHONPATH="${r"${PYTHONPATH}"}"
    export PYTHONPATH="$PYTHONPATH:python"
    # start hwc-component ${hwcPythonFile}
    python3 -u "python/${hwcPythonFile}" --name ${pair.getValue()} > "python/${hwcPythonFile}.log" 2>&1 &
    export PYTHONPATH="$OLD_PYTHONPATH"
    sleep 1 # wait for interpreted code to be ready - control MQTT ports MUST be subscribed to work
  </#if>
</#list>

<#list instances as pair >
    <#if brokerIsMQTT>
    ./${pair.getKey().fullName} --name ${pair.getValue()} --brokerHostname localhost --brokerPort 1883  --localHostname localhost > ${pair.getValue()}.log 2>&1 &
    <#elseif brokerIsDDS>
      <#if splittingModeIsDistributed>
        ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini --DCPSInfoRepo localhost:12345 > ${pair.getValue()}.log 2>&1 &
      <#else>
        ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini > ${pair.getValue()}.log 2>&1 &
      </#if>
    <#else>
  ./${pair.getKey().fullName} --name ${pair.getValue()} --managementPort ${config.getComponentPortMap().getManagementPort(pair.getValue())} --dataPort ${config.getComponentPortMap().getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
  </#if>
</#list>
<#if brokerIsMQTT>
  <#list sensorActuatorPorts as port >
  ./${port} --name ${port} --brokerPort 1883 --localHostname localhost > ${port}.log 2>&1 &
  </#list>
</#if>

<#if brokerIsMQTT && hwcPythonScripts?size!=0>
exec bash -c 'export PYTHONPATH=$PYTHONPATH:../../python; python3 -u "python/sensoractuatormanager.py" > "python/sensoractuatormanager.log" 2>&1 &' '{}' \;
</#if>