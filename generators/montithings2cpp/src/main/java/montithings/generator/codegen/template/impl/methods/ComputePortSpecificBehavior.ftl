<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("behavior", "comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics}
${className}${generics}::compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}
<#if ComponentHelper.hasTest(comp)>
    (${compname}Input${generics} ${Identifier.getInputName()}, sole::uuid uuid)
<#else>
    (${compname}Input${generics} ${Identifier.getInputName()})
</#if>
{
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};
${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementStart", [comp, config])}

<#if ComponentHelper.hasTest(comp)>
    ${ComponentHelper.printTestBlock(behavior)}
</#if>

${ComponentHelper.printJavaBlock(behavior.getMCJavaBlock(), logTracingEnabled)}

<#if ComponentHelper.hasTest(comp)>}</#if>

${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementEnd", [comp, config])}
<#list ComponentHelper.getPublishedPorts(comp, behavior.getMCJavaBlock()) as port>
    ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}