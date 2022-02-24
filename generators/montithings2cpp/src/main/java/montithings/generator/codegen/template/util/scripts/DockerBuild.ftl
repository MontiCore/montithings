<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

<#if splittingModeDisabled>
  docker build -t ${comp.getFullName()?lower_case}:latest .
<#else>
  <#-- helper list to detect duplicated keys -->
  <#assign processedInstances = [] />

  <#list instances as pair >
    <#if ! processedInstances?seq_contains(pair.getKey().fullName)>
      <#assign processedInstances = processedInstances + [pair.getKey().fullName] />

      docker build --target ${pair.getKey().fullName} -t ${pair.getKey().fullName?lower_case}:latest .
    </#if>
  </#list>
</#if>
<#if brokerIsMQTT>
  <#list sensorActuatorPorts as port >

      docker build --target ${port} -t ${port?lower_case}:latest .
  </#list>
  <#list hwcPythonScripts as script >

      docker build --target ${script} -t ${script?lower_case}:latest .
  </#list>
    <#if hwcPythonScripts?size!=0>
      docker build --target sensoractuatormanager -t sensoractuatormanager:latest .
    </#if>
</#if>