<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${className}() = default;
<#if comp.getAllIncomingPorts()?has_content && !isBatch>
  explicit ${className}(
    <#list comp.getAllIncomingPorts() as port>
      <#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
      <#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>
      tl::optional<${type}> ${port.getName()}
        <#sep>,</#sep>
    </#list>);
</#if>