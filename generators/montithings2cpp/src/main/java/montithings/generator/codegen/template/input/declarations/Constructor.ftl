<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/GeneralPreamble.ftl">

${className}() = default;
<#if comp.getAllIncomingPorts()?has_content && !isBatch>
  explicit ${className}(
    <#list comp.getAllIncomingPorts() as port>
      <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>
      tl::optional<Message<${type}>> ${port.getName()}
        <#sep>,</#sep>
    </#list>);
</#if>