<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/input/helper/GeneralPreamble.ftl">

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>
  <#if ComponentHelper.hasAgoQualification(comp, port)>
    std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, Message<${type}>>> ${className}::dequeOf__${port.getName()?cap_first};
  </#if>
</#list>
