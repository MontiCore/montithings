<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("everyBlock", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/impl/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics}
${className}${generics}::compute${ComponentHelper.getEveryBlockName(comp, everyBlock)}
(${compname}Input${generics} ${Identifier.getInputName()})
{
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};
${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementStart", [comp, config])}
${ComponentHelper.printJavaBlock(everyBlock.getMCJavaBlock(), logTracingEnabled)}
${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementEnd", [comp, config])}
<#list ComponentHelper.getPublishedPorts(comp, everyBlock.getMCJavaBlock()) as port>
    ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}