<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#assign isLogTracingEnabled = config.getLogTracing().toString() == "ON">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${className}${generics}::compute(${compname}Input${generics}
${Identifier.getInputName()}){
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};

${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementStart", [comp, config])}

<#if ComponentHelper.hasBehavior(comp)>
  ${ComponentHelper.printStatementBehavior(comp, isLogTracingEnabled)}
  <#list ComponentHelper.getPublishedPortsForBehavior(comp) as port>
    ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
  </#list>
<#else>
  ${tc.includeArgs("template.util.statechart.hooks.Compute", [comp, config])}
</#if>

${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementEnd", [comp, config])}
return ${Identifier.getResultName()};
}