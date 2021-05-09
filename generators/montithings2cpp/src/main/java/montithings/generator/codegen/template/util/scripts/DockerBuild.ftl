<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

<#if config.getSplittingMode().toString() == "OFF">
  docker build -t ${comp.getFullName()?lower_case}:latest .
<#else>
  <#list instances as pair >
    docker build --target ${pair.getKey().fullName} -t ${pair.getKey().fullName?lower_case}:latest .
  </#list>
</#if>