<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>

<#if !isBatch>
  <#if !(comp.getAllIncomingPorts()?size == 0)>
    ${tc.includeArgs("template.input.methods.Constructor", [comp, config, existsHWC])}
  </#if>
</#if>

<#list ComponentHelper.getPortsInBatchStatement(comp) as port>
  <#if port.isIncoming()>
    ${tc.includeArgs("template.input.methods.GetBatchPort", [port, comp, config, existsHWC])}
    ${tc.includeArgs("template.input.methods.AddBatchPort", [port, comp, config, existsHWC])}
    ${tc.includeArgs("template.input.methods.SetBatchPort", [port, comp, config, existsHWC])}
  </#if>
</#list>

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
  <#if port.isIncoming()>
    ${tc.includeArgs("template.input.methods.GetPort", [port, comp, config, existsHWC])}
    ${tc.includeArgs("template.input.methods.SetPort", [port, comp, config, existsHWC])}

    <#if TypesHelper.getCDEReplacement(port, config).isPresent()>
      ${tc.includeArgs("template.input.methods.GetPortAdap", [port, comp, config, existsHWC])}
      ${tc.includeArgs("template.input.methods.SetPortAdap", [port, comp, config, existsHWC])}
    </#if>

    <#if ComponentHelper.hasAgoQualification(comp, port)>
      std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${TypesPrinter.getRealPortCppTypeString(comp, port, config)}>> ${className}${Utils.printFormalTypeParameters(comp, false)}::dequeOf__${port.getName()?cap_first};
      ${tc.includeArgs("template.input.methods.AgoGetPort", [port, comp, config, existsHWC])}
      ${tc.includeArgs("template.input.methods.CleanDequeOfPort", [port, comp, config, existsHWC])}
    </#if>
  </#if>
</#list>