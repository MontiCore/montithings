<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

<#if config.getSplittingMode().toString() == "OFF">
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