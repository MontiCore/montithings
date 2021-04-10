<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/result/helper/GeneralPreamble.ftl">

${tc.includeArgs("template.result.methods.Constructor", [comp, config, existsHWC])}

<#list comp.getOutgoingPorts() as port>
  ${tc.includeArgs("template.result.methods.GetPort", [port, comp, config, existsHWC])}
  ${tc.includeArgs("template.result.methods.SetPort", [port, comp, config, existsHWC])}

  <#if ComponentHelper.getCDEReplacement(port, config).isPresent()>
    ${tc.includeArgs("template.result.methods.GetPortAdap", [port, comp, config, existsHWC])}
    ${tc.includeArgs("template.result.methods.SetPortAdap", [port, comp, config, existsHWC])}
  </#if>

  <#if ComponentHelper.hasAgoQualification(comp, port)>
    std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>> ${className}${Utils.printFormalTypeParameters(comp, false)}::dequeOf__${port.getName()?cap_first};
    ${tc.includeArgs("template.result.methods.AgoGetPort", [port, comp, config, existsHWC])}
    ${tc.includeArgs("template.result.methods.CleanDequeOfPort", [port, comp, config, existsHWC])}
  </#if>
</#list>