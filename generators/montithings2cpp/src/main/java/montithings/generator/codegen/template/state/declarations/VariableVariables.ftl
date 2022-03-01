<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign varName = var.getName()>
<#assign varType = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>

<#if ComponentHelper.hasAgoQualification(comp, var)>
  std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${varType}>> dequeOf__${varName?cap_first};
  std::chrono::nanoseconds highestAgoOf__${varName?cap_first} = std::chrono::nanoseconds {${ComponentHelper.getHighestAgoQualification(comp, varName)}};
  void cleanDequeOf${varName?cap_first}(std::chrono::time_point<std::chrono::system_clock> now);
</#if>