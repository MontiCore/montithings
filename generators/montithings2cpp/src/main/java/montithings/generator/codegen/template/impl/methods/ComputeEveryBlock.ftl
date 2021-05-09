<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("everyBlock", "comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#assign isLogTracingEnabled = config.getLogTracing().toString() == "ON">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics}
${className}${generics}::compute${ComponentHelper.getEveryBlockName(comp, everyBlock)}
(${compname}Input${generics} ${Identifier.getInputName()})
{
${compname}Result${generics} ${Identifier.getResultName()};
${ComponentHelper.printJavaBlock(everyBlock.getMCJavaBlock(), isLogTracingEnabled)}
<#list ComponentHelper.getPublishedPorts(comp, everyBlock.getMCJavaBlock()) as port>
    ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}