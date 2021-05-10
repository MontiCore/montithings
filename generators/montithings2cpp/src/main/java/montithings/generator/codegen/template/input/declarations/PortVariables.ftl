<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()};
  <#if ComponentHelper.hasAgoQualification(comp, port)>
    static std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>> dequeOf__${port.getName()?cap_first};
    std::chrono::nanoseconds highestAgoOf__${port.getName()?cap_first} = std::chrono::nanoseconds {${ComponentHelper.getHighestAgoQualification(comp, port.getName())}};
    void cleanDequeOf${port.getName()?cap_first}(std::chrono::time_point<std::chrono::system_clock> now);
  </#if>
</#list>

<#list ComponentHelper.getPortsInBatchStatement(comp) as port >
  std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()} = {};
</#list>