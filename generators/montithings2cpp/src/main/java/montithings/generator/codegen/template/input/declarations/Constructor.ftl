<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${className}() = default;
<#if comp.getAllIncomingPorts()?has_content && !isBatch>
  explicit ${className}(
    <#list comp.getAllIncomingPorts() as port>
      tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()}
        <#sep>,</#sep>
    </#list>);
</#if>