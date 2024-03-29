<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>

${tc.includeArgs("template.input.declarations.InitStaticVariables", [comp, config])}

<#if !isBatch>
  <#if ComponentHelper.componentHasIncomingPorts(comp)>
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
      ${tc.includeArgs("template.input.methods.AgoGetPort", [port, comp, config, existsHWC])}
      ${tc.includeArgs("template.input.methods.CleanDequeOfPort", [port, comp, config, existsHWC])}
    </#if>
  </#if>
</#list>