<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/bash
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

if [ $# -eq 0 ]; then
  REGISTRY=""
elif [[ "$1" == */ ]]; then
  REGISTRY=$1
else
  REGISTRY="$1/"
fi


<#if splittingModeDisabled>
  docker build -t ${comp.getFullName()?lower_case}:latest .
<#else>
  <#-- helper list to detect duplicated keys -->
  <#assign processedInstances = [] />

  <#list instances as pair >
    <#if ! processedInstances?seq_contains(pair.getKey().fullName)>
      <#assign processedInstances = processedInstances + [pair.getKey().fullName] />

      docker build --target ${pair.getKey().fullName} -t "${"$REGISTRY"}"${pair.getKey().fullName?lower_case}:latest .
      if [ $2 -eq 1 ]; then
        docker push "${"$REGISTRY"}"${pair.getKey().fullName?lower_case}:latest
      fi
    </#if>
  </#list>
</#if>
<#if brokerIsMQTT>
  <#list sensorActuatorPorts as port >

      docker build --target ${port} -t "${"$REGISTRY"}"${port?lower_case}:latest .
      if [ $2 -eq 1 ]; then
      docker push "${"$REGISTRY"}"${port?lower_case}:latest
      fi
  </#list>
  <#list hwcPythonScripts as script >

      docker build --target ${script} -t "${"$REGISTRY"}"${script?lower_case}:latest .
      if [ $2 -eq 1 ]; then
      docker push "${"$REGISTRY"}"${script?lower_case}:latest
      fi
  </#list>
    <#if hwcPythonScripts?size!=0>
      docker build --target sensoractuatormanager -t "${"$REGISTRY"}"sensoractuatormanager:latest .
      if [ $2 -eq 1 ]; then
      docker push "${"$REGISTRY"}"sensoractuatormanager:latest
      fi
    </#if>
</#if>
