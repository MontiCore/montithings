<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

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