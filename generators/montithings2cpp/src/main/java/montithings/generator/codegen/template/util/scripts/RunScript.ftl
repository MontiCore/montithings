<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
echo "Starting DCPSInfoRepo..."
docker run --name dcpsinforepo --rm -d -p 12345:12345 registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo
echo "Waiting 5 seconds..."
sleep 5
echo "Starting components..."
</#if>

<#list instances as pair >
  <#if config.getMessageBroker().toString() == "MQTT">
  ./${pair.getKey().fullName} --name ${pair.getValue()} --brokerHostname localhost --brokerPort 1883 > ${pair.getValue()}.log 2>&1 &
  <#elseif config.getMessageBroker().toString() == "DDS">
    <#if config.getSplittingMode().toString() == "DISTRIBUTED">
      ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini --DCPSInfoRepo localhost:12345 > ${pair.getValue()}.log 2>&1 &
    <#else>
      ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini > ${pair.getValue()}.log 2>&1 &
    </#if>
  <#else>
  ./${pair.getKey().fullName} --name ${pair.getValue()} --managementPort ${config.getComponentPortMap().getManagementPort(pair.getValue())} --dataPort ${config.getComponentPortMap().getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
  </#if>
</#list>

# Run Python Ports
find hwc -name "*.py" -exec bash -c 'export PYTHONPATH=$PYTHONPATH:../../python; python3 "$0" > "$0.log" 2>&1 &' '{}' \;