<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/result/helper/GeneralPreamble.ftl">

<#assign name = port.getName()>
<#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

tl::optional<${type}> ${port.getName()};

<#if ComponentHelper.hasAgoQualification(comp, port)>
  <#assign maxTime = ComponentHelper.getHighestAgoQualification(comp, name)>
  static std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${type}>> dequeOf__${name?cap_first};
  std::chrono::nanoseconds highestAgoOf__${name?cap_first} = std::chrono::nanoseconds {${maxTime}};
  void cleanDequeOf${name?cap_first}(std::chrono::time_point<std::chrono::system_clock> now);
</#if>